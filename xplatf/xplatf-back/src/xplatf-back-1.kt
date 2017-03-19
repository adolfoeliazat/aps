package aps.back

import aps.*
import org.springframework.data.repository.findOrDie
import javax.servlet.http.HttpServletResponse

class FuckSomeoneParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val bpc: BitchyProcedureContext,
    val req: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res,
    val validate: (ProcedureContext, Req) -> Unit = { ctx, req -> },
    val wrapInFormResponse: Boolean,
    @Deprecated("Kill me") val needsDB: Boolean,
    val needsDangerousToken: Boolean,
    val needsUser: NeedsUser,
    val userKinds: Set<UserKind>,
    val considerNextRequestTimestampFiddling: Boolean,
    val logRequestJSON: Boolean
)

fun <Req : RequestMatumba, Res : CommonResponseFields>
    fuckSomeone(p: FuckSomeoneParams<Req, Res>)
{
    object {
        lateinit var responseBean: CommonResponseFields
        val log = debugLog
        val ctx = ProcedureContext()

        init {
            RequestGlobus.procedureCtx = ctx
            try {
                p.bpc.servletRequest.characterEncoding = "UTF-8"
                val requestJSON = p.bpc.servletRequest.reader.readText()
                if (p.logRequestJSON) {
                    log.info("${p.bpc.servletRequest.pathInfo}: $requestJSON")
                }
                val rmap = hackyObjectMapper.readValue(requestJSON, Map::class.java)
                RequestGlobus.commonRequestFields = hackyObjectMapper.readValue(requestJSON, CommonRequestFieldsHolder::class.java)
                // log.section("rmap:", rmap)

                fun serviceShit() {
                    (rmap["wideClientKind"] as String).let {
                        when (it) {
                            WideClientKind.User::class.simpleName -> {
                                ctx.clientKind = ClientKind.valueOf(rmap["clientKind"] as String)
                                ctx.wideClientKind = WideClientKind.User(ctx.clientKind)
                                ctx.clientDomain = when (ctx.clientKind) {
                                    ClientKind.UA_CUSTOMER -> "aps-ua-customer.local"
                                    ClientKind.UA_WRITER -> "aps-ua-writer.local"
                                }
                                ctx.clientPortSuffix = when (ctx.clientKind) {
                                    ClientKind.UA_CUSTOMER -> ":3012"
                                    ClientKind.UA_WRITER -> ":3022"
                                }
                            }
                            WideClientKind.Test::class.simpleName -> {
                                ctx.wideClientKind = WideClientKind.Test()
                            }
                            else -> wtf("wideClientKind: $it")
                        }
                    }
                    ctx.lang = Language.valueOf(rmap["lang"] as String)

                    fun runShitWithMaybeDB(): Res {
                        if (p.needsUser != NeedsUser.NO) {
                            val token = rmap["token"] as String?
                            if (token == null) {
                                if (p.needsUser == NeedsUser.YES)
                                    bitch("I want freaking token")
                                ctx.hasUser = false
                            } else {
                                ctx.token = token
                                val u = userByToken2(ctx.token)
                                ctx.user = u
                                ctx.user_killme = u.toRTO(searchWords = listOf())
                                if (!p.userKinds.contains(ctx.user_killme.kind))
                                    bitch("User kind not allowed: ${ctx.user_killme.kind}")
                                ctx.hasUser = true
                            }
                        }

                        val input  = rmap["fields"] as Map<String, Any?>
                        val req = p.req(ctx)
                        for (field in req._fields) field.load(input, ctx.fieldErrors)

                        if (p.needsDangerousToken) {
                            if (rmap["token"] != systemDangerousToken()) {
                                bitch("Invalid dangerous token")
                            }
                            RequestGlobus.shitIsDangerous = true
                        } else {
                            RequestGlobus.shitIsDangerous = false
                        }

                        p.validate(ctx, req)
                        if (ctx.fieldErrors.isNotEmpty()) bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))

                        if (!RequestGlobus.shitIsDangerous) {
                            RequestGlobus.requesterOrAnonymous = ctx.user ?: when (ctx.clientKind) {
                                ClientKind.UA_CUSTOMER -> userRepo.findOrDie(const.userID.anonymousCustomer)
                                ClientKind.UA_WRITER -> userRepo.findOrDie(const.userID.anonymousWriter)
                            }
                            RequestGlobus.requesterOrAnonymousInitialFields = RequestGlobus.requesterOrAnonymous.user.copy()
                        }

                        return p.runShit(ctx, req)
                    }

                    val res = if (p.needsDB) {
                        if (TestServerFiddling.rejectAllRequestsNeedingDB) bitch("Fuck you. I mean nothing personal, I do this to everyone...")

//                        val db = DB.byID(RequestGlobus.commonRequestFields.databaseID!!)

//                        db.joo {q->
//                            ctx.q = q
                        runShitWithMaybeDB()
//                        }
                    } else {
                        runShitWithMaybeDB()
                    }
                    res.backendVersion = BackGlobus.version

                    responseBean = if (p.wrapInFormResponse) FormResponse.Hunky(res) else res
                }

                val pathInfo = p.bpc.servletRequest.pathInfo
                serviceShit()
//                if (pathInfo.contains("privilegedRedisCommand"))
//                    serviceShit()
//                else
//                    redisLog.group("Request: $pathInfo", ::serviceShit)
            }
            catch (e: ExpectedRPCShit) {
                if (p.wrapInFormResponse) {
                    log.info("Softened RPC shit: ${e.message}")
                    responseBean = FormResponse.Shitty(e.message, ctx.fieldErrors)
                } else {
                    throw e
                }
            }

            responseBean.backendVersion = BackGlobus.version

            p.bpc.servletResponse-{o->
                o.contentType = "application/json; charset=utf-8"
                o.writer.println(shittyObjectMapper.writeValueAsString(responseBean))
//                o.writer.println(hackyObjectMapper.writeValueAsString(responseBean))
                o.status = HttpServletResponse.SC_OK
            }
        }
    }
}


