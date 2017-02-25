/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.jooq.DSLContext
import java.sql.Timestamp
import java.util.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.Hibernate
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.Delegates.notNull

fun systemDangerousToken(): String = System.getenv("APS_DANGEROUS_TOKEN") ?: die("I want APS_DANGEROUS_TOKEN environment variable")

// typealias ServletService = (HttpServletRequest, HttpServletResponse) -> Unit

class ProcedureContext {
    var q by notNullOnce<DSLContext>()
    var wideClientKind by notNullOnce<WideClientKind>()
    var clientKind by notNullOnce<ClientKind>()
    var lang by notNullOnce<Language>()
    var clientDomain by notNullOnce<String>()
    var clientPortSuffix by notNullOnce<String>()
    var user_killme by notNullOnce<UserRTO>()
    var token by notNullOnce<String>()
    var hasUser by notNullOnce<Boolean>()
    var user: User? = null

    val fieldErrors = mutableListOf<FieldError>()

    val xlobal = object:Xlobal {
        val ctx = this@ProcedureContext

        override val user get()=
            if (ctx.hasUser) ctx.user_killme
            else null
    }

    val clientProtocol = "http" // TODO:vgrechka Switch everything to HTTPS
    val clientRootPath = ""

    val clientRoot get()= "$clientProtocol://$clientDomain$clientPortSuffix$clientRootPath"
}

enum class NeedsUser {
    YES, NO, MAYBE
}

class ProcedureSpec<Req : RequestMatumba, Res : Any>(
    val req: (ProcedureContext) -> Req,
    val runShit: (ProcedureContext, Req) -> Res,
    val validate: (ProcedureContext, Req) -> Unit = { ctx, req -> },
    val wrapInFormResponse: Boolean,
    val needsDB: Boolean,
    val needsDangerousToken: Boolean,
    val needsUser: NeedsUser,
    val userKinds: Set<UserKind>,
    val considerNextRequestTimestampFiddling: Boolean,
    val logRequestJSON: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
class CommonRequestFieldsHolder : CommonRequestFields {
    override var rootRedisLogMessageID: String? = null
    override var databaseID: String? = null
    override var fakeEmail = false
    override lateinit var clientURL: String
}

fun <Req : RequestMatumba, Res : CommonResponseFields>
remoteProcedure(spec: ProcedureSpec<Req, Res>): (HttpServletRequest, HttpServletResponse) -> Unit =
    fun(servletRequest, servletResponse) { object {
        lateinit var responseBean: CommonResponseFields
        val log = debugLog
        val ctx = ProcedureContext()

        init {
            try {
                servletRequest.characterEncoding = "UTF-8"
                val requestJSON = servletRequest.reader.readText()
                if (spec.logRequestJSON) {
                    log.info("${servletRequest.pathInfo}: $requestJSON")
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
                        if (spec.needsUser != NeedsUser.NO) {
                            val token = rmap["token"] as String?
                            if (token == null) {
                                if (spec.needsUser == NeedsUser.YES)
                                    bitch("I want freaking token")
                                ctx.hasUser = false
                            } else {
                                ctx.token = token
                                val u = userByToken2(ctx.token)
                                ctx.user = u
                                ctx.user_killme = u.toRTO(searchWords = listOf())
                                if (!spec.userKinds.contains(ctx.user_killme.kind))
                                    bitch("User kind not allowed: ${ctx.user_killme.kind}")
                                ctx.hasUser = true
                            }
                        }

                        val input  = rmap["fields"] as Map<String, Any?>
                        val req = spec.req(ctx)
                        for (field in req._fields) field.load(input, ctx.fieldErrors)

                        if (spec.needsDangerousToken) {
                            if (rmap["token"] != systemDangerousToken()) {
                                bitch("Invalid dangerous token")
                            }
                        }

                        spec.validate(ctx, req)
                        if (ctx.fieldErrors.isNotEmpty()) bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))

                        return spec.runShit(ctx, req)
                    }

                    val res = if (spec.needsDB) {
                        if (TestServerFiddling.rejectAllRequestsNeedingDB) bitch("Fuck you. I mean nothing personal, I do this to everyone...")

//                        val db = DB.apsTestOnTestServer
                        val db = DB.byID(RequestGlobus.commonRequestFields.databaseID!!)

//                        redisLog.group("Some shit 2") {
                            db.joo {q->
                                ctx.q = q
//                                ctx.qshit = DSLContextProxyFactory(q)
                                runShitWithMaybeDB()
                            }
//                        }
                    } else {
                        runShitWithMaybeDB()
                    }
                    res.backendVersion = BackGlobus.version

                    responseBean = if (spec.wrapInFormResponse) FormResponse.Hunky(res) else res
                }

                val pathInfo = servletRequest.pathInfo
                if (pathInfo.contains("privilegedRedisCommand"))
                    serviceShit()
                else
                    redisLog.group("Request: $pathInfo", ::serviceShit)
            }
            catch (e: ExpectedRPCShit) {
                if (spec.wrapInFormResponse) {
                    log.info("Softened RPC shit: ${e.message}")
                    responseBean = FormResponse.Shitty(e.message, ctx.fieldErrors)
                } else {
                    throw e
                }
            }

            responseBean.backendVersion = BackGlobus.version

            servletResponse-{o->
                o.contentType = "application/json; charset=utf-8"
                o.writer.println(hackyObjectMapper.writeValueAsString(responseBean))
                o.status = HttpServletResponse.SC_OK
            }
        }
    }
}

//fun <Req> void(shit: (ProcedureContext, Req) -> Unit): (ProcedureContext, Req) -> GenericResponse = {ctx, req ->
//    shit(ctx, req)
//    GenericResponse()
//}

fun <Req : RequestMatumba, Res : CommonResponseFields>
publicProcedure(req: (ProcedureContext) -> Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null, validate: ((ProcedureContext, Req) -> Unit)? = null, needsDB: Boolean? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        validate = validate ?: {ctx, req ->},
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = needsDB ?: true,
        needsDangerousToken = false,
        needsUser = NeedsUser.NO,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : CommonResponseFields>
anyUserProcedure(req: (ProcedureContext) -> Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.YES,
        userKinds = setOf(UserKind.CUSTOMER, UserKind.WRITER, UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : CommonResponseFields>
customerProcedure(req: (ProcedureContext) -> Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null, needsUser: NeedsUser? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = needsUser ?: NeedsUser.YES,
        userKinds = setOf(UserKind.CUSTOMER),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : CommonResponseFields>
writerProcedure(req: (ProcedureContext) -> Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.YES,
        userKinds = setOf(UserKind.WRITER),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : CommonResponseFields>
adminProcedure(
    req: (ProcedureContext) -> Req,
    runShit: (ProcedureContext, Req) -> Res,
    wrapInFormResponse: Boolean? = null,
    validate: ((ProcedureContext, Req) -> Unit)? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =

    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        validate = validate ?: {ctx, req ->},
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = NeedsUser.YES,
        userKinds = setOf(UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

//fun userByToken(q: DSLContext, token: String): UserRTO {
//    val rows = tracingSQL("Select token") {q
//        .select().from(USER_TOKENS, USERS)
//        .where(USER_TOKENS.TOKEN.eq(token))
//        .and(USERS.ID.eq(USER_TOKENS.USER_ID))
//        .fetch().into(JQUsers::class.java)
//    }
//    if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2
//
//    // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1
//
//    return rows[0].toRTO(q)
//}

fun userByToken2(token: String): User {
    val ut = userTokenRepo.findByToken(token) ?: bitch("Invalid token")
    return ut.user!!
}





















