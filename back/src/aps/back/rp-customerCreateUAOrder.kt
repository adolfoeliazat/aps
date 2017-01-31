/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class ServeShit {
    var servletRequest by notNullOnce<HttpServletRequest>()
    var servletResponse by notNullOnce<HttpServletResponse>()

    abstract fun serve()
}

@Component @Scope(SCOPE_PROTOTYPE) class ServeUACustomerCreateOrder(
    val repo: UAOrderRepository
) : ServeShit() {
    override fun serve() {
        serveCustomerShit(ServeCustomerShitParams(
            shit = this,
            req = {UACustomerCreateOrderRequest(it.xlobal)},
            needsUser = NeedsUser.MAYBE,
            runShit = {_, req: UACustomerCreateOrderRequest ->
                repo.save(UAOrder(title = req.documentTitle.value))
                die("cooooooool")
            }
        ))
    }
}

class ServeCustomerShitParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val shit: ServeShit,
    val req: (ProcedureContext) -> Req,
    val needsUser: NeedsUser,
    val runShit: (ProcedureContext, Req) -> Res)






fun <Req : RequestMatumba, Res : CommonResponseFields>
    serveCustomerShit(p: ServeCustomerShitParams<Req, Res>)
{
    serveShit(ServeShitParams(
        shit = p.shit,
        req = p.req,
        runShit = p.runShit,
        wrapInFormResponse = true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = p.needsUser,
        userKinds = setOf(UserKind.CUSTOMER),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true
    ))
}

class ServeShitParams<Req : RequestMatumba, out Res : CommonResponseFields>(
    val shit: ServeShit,
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

fun <Req : RequestMatumba, Res : CommonResponseFields>
    serveShit(p: ServeShitParams<Req, Res>)
{
    object {
        lateinit var responseBean: CommonResponseFields
        val log = debugLog
        val ctx = ProcedureContext()

        init {
            try {
                p.shit.servletRequest.characterEncoding = "UTF-8"
                val requestJSON = p.shit.servletRequest.reader.readText()
                if (p.logRequestJSON) {
                    log.info("${p.shit.servletRequest.pathInfo}: $requestJSON")
                }
                val rmap = hackyObjectMapper.readValue(requestJSON, Map::class.java)
                requestShit.commonRequestFields = hackyObjectMapper.readValue(requestJSON, CommonRequestFieldsHolder::class.java)
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

                    var ts = Timestamp(Date().time)
                    if (p.considerNextRequestTimestampFiddling) {
                        TestServerFiddling.nextRequestTimestamp?.let {
                            TestServerFiddling.nextRequestTimestamp = null
                            ts = it
                        }
                    }
                    ctx.requestTimestamp = ts

                    fun runShitWithMaybeDB(): Res {
                        if (p.needsUser != NeedsUser.NO) {
                            val token = rmap["token"] as String?
                            if (token == null) {
                                if (p.needsUser == NeedsUser.YES)
                                    bitch("I want freaking token")
                                ctx.hasUser = false
                            } else {
                                ctx.token = token
                                ctx.user = userByToken(ctx.q, ctx.token)
                                if (!p.userKinds.contains(ctx.user.kind))
                                    bitch("User kind not allowed: ${ctx.user.kind}")
                                ctx.hasUser = true
                            }
                        }

                        val input  = rmap["fields"] as Map<String, Any?>
                        val req = p.req(ctx)
                        for (field in req.fields) field.load(input, ctx.fieldErrors)

                        if (p.needsDangerousToken) {
                            if (rmap["token"] != systemDangerousToken()) {
                                bitch("Invalid dangerous token")
                            }
                        }

                        p.validate(ctx, req)
                        if (ctx.fieldErrors.isNotEmpty()) bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))

                        return p.runShit(ctx, req)
                    }

                    val res = if (p.needsDB) {
                        if (TestServerFiddling.rejectAllRequestsNeedingDB) bitch("Fuck you. I mean nothing personal, I do this to everyone...")

                        val db = DB.byID(requestShit.commonRequestFields.databaseID!!)

                        db.joo {q->
                            ctx.q = q
                            runShitWithMaybeDB()
                        }
                    } else {
                        runShitWithMaybeDB()
                    }
                    res.backendVersion = BackGlobus.version

                    responseBean = if (p.wrapInFormResponse) FormResponse.Hunky(res) else res
                }

                val pathInfo = p.shit.servletRequest.pathInfo
                if (pathInfo.contains("privilegedRedisCommand"))
                    serviceShit()
                else
                    redisLog.group("Request: $pathInfo", ::serviceShit)
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

            p.shit.servletResponse-{o->
                o.contentType = "application/json; charset=utf-8"
                o.writer.println(hackyObjectMapper.writeValueAsString(responseBean))
                o.status = HttpServletResponse.SC_OK
            }
        }
    }
}




















//@RemoteProcedureFactory fun _serveUACustomerCreateOrder() = customerProcedure(
//    {UACustomerCreateOrderRequest(it.xlobal)},
//    needsUser = NeedsUser.MAYBE,
//    runShit = fun(ctx, req): UACustomerCreateOrderRequest.Response {
//        val documentType = req.documentType.value
//
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//        springctx.getBean(WarmWelcomer::class.java).sayHello()
//
//        val repo = springctx.getBean(UAOrderRepository::class.java)
//        repo.save(UAOrder(title = "boobs"))
//        dwarnStriking("Saved shit")
//
//        val shit = repo.findOne(1)
//        dwarnStriking("Found shit", shit.id, shit.title)
//        shit.title = "cunt"
//        repo.save(shit)
//
//        die()
//
////        val orderID = UA_ORDERS.let {t->
////            ctx.insertShit("Insert order", t) {it
////                .set(t.CUSTOMER_ID, ctx.user.id.toLong())
////                .set(t.TITLE, req.documentTitle.value)
////                .set(t.ADMIN_NOTES, "")
////                .set(t.DOCUMENT_TYPE, documentType.toJOOQ())
////                .set(t.NUM_PAGES, req.numPages.value)
////                .set(t.NUM_SOURCES, req.numSources.value)
////                .set(t.DETAILS, req.documentDetails.value)
////                .set(t.STATE, JQUaOrderState.LOOKING_FOR_WRITERS)
////                .returnID(t)
////            }
////        }
//
////        fun createArea(name: String) {
////            tracingSQL("Insert order area: $name") {ctx.q
////                .insertInto(UA_ORDER_AREAS)
////                .set(UA_ORDER_AREAS.INSERTED_AT, ctx.requestTimestamp)
////                .set(UA_ORDER_AREAS.UPDATED_AT, ctx.requestTimestamp)
////                .set(UA_ORDER_AREAS.UA_ORDER_ID, orderID)
////                .set(UA_ORDER_AREAS.NAME, name)
////                .execute()
////            }
////        }
////
////        createArea(const.orderArea.customer)
////        createArea(const.orderArea.writer)
//
////        return UACustomerCreateOrderRequest.Response(orderID.toString())
//    }
//)
