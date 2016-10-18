/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import org.jooq.DSLContext
import java.sql.Timestamp
import java.util.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.Users
import org.jooq.Field
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass

fun systemDangerousToken(): String = System.getenv("APS_DANGEROUS_TOKEN") ?: die("I want APS_DANGEROUS_TOKEN environment variable")

// typealias ServletService = (HttpServletRequest, HttpServletResponse) -> Unit

class ProcedureContext {
    lateinit var q: DSLContext
    lateinit var clientKind: ClientKind
    lateinit var lang: Language
    lateinit var requestTimestamp: Timestamp
    lateinit var clientDomain: String
    lateinit var clientPortSuffix: String
    lateinit var user: UserRTO

    val fieldErrors = mutableListOf<FieldError>()
}

class ProcedureSpec<Req : RequestMatumba, Res : Any>(
    val req: Req,
    val runShit: (ProcedureContext, Req) -> Res,
    val validate: (ProcedureContext, Req) -> Unit = { ctx, req -> },
    val wrapInFormResponse: Boolean,
    val needsDB: Boolean,
    val needsDangerousToken: Boolean,
    val needsUser: Boolean,
    val userKinds: Set<UserKind>,
    val considerNextRequestTimestampFiddling: Boolean,
    val logRequestJSON: Boolean
)

fun <Req : RequestMatumba, Res : Any>
remoteProcedure(spec: ProcedureSpec<Req, Res>): (HttpServletRequest, HttpServletResponse) -> Unit  = {servletRequest, servletResponse ->
    var responseBean: Any
    val log = debugLog
    val ctx = ProcedureContext()

    try {
        servletRequest.characterEncoding = "UTF-8"
        val requestJSON = servletRequest.reader.readText()
        if (spec.logRequestJSON) {
            log.info("${servletRequest.pathInfo}: $requestJSON")
        }
        val rmap = hackyObjectMapper.readValue(requestJSON, Map::class.java)
        // log.section("rmap:", rmap)

        ctx.clientKind = ClientKind.valueOf(rmap["clientKind"] as String)
        ctx.lang = Language.valueOf(rmap["lang"] as String)

        ctx.requestTimestamp = Timestamp(Date().time)
        if (spec.considerNextRequestTimestampFiddling) {
            TestServerFiddling.nextRequestTimestamp?.let {
                TestServerFiddling.nextRequestTimestamp = null
                ctx.requestTimestamp = it
            }
        }

        ctx.clientDomain = when (ctx.lang) {
            Language.EN -> when (ctx.clientKind) {
                ClientKind.CUSTOMER -> "aps-en-customer.local"
                ClientKind.WRITER -> "aps-en-writer.local"
            }
            Language.UA -> when (ctx.clientKind) {
                ClientKind.CUSTOMER -> "aps-ua-customer.local"
                ClientKind.WRITER -> "aps-ua-writer.local"
            }
        }

        ctx.clientPortSuffix = when (ctx.lang) {
            Language.EN -> when (ctx.clientKind) {
                ClientKind.CUSTOMER -> ":3011"
                ClientKind.WRITER -> ":3021"
            }
            Language.UA -> when (ctx.clientKind) {
                ClientKind.CUSTOMER -> ":3012"
                ClientKind.WRITER -> ":3022"
            }
        }

        fun runShitWithMaybeDB(): Res {
            // TODO:vgrechka Wrap each RPC in transaction    5928def7-392e-433f-99a8-9decfe959971

            val input  = rmap["fields"] as Map<String, Any?>
            for (field in spec.req.fields) field.load(input, ctx.fieldErrors)

            if (spec.needsDangerousToken) {
                if (rmap["token"] != systemDangerousToken()) {
                    // TODO:vgrechka Notify me about hackers    50ec0187-3b47-43de-8a29-b561e6d7132f
                    bitch("Invalid dangerous token")
                }
            }

            if (spec.needsUser) {
                val token = rmap["token"] as String
                val rows = ctx.q.select()
                    .from(USER_TOKENS, USERS)
                    .where(USER_TOKENS.TOKEN.eq(token))
                    .and(USERS.ID.eq(USER_TOKENS.USER_ID))
                    .fetch().into(Users::class.java)
                if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2

                // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1

                ctx.user = rows[0].toRTO(ctx.q)

                if (!spec.userKinds.contains(ctx.user.kind)) bitch("User kind not allowed: ${ctx.user.kind}")
            }

            spec.validate(ctx, spec.req)
            if (ctx.fieldErrors.isNotEmpty()) bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))

            return spec.runShit(ctx, spec.req)
        }

        val res = if (spec.needsDB) {
            val db = DB.apsTestOnTestServer
            db.joo{q ->
                ctx.q = q
                runShitWithMaybeDB()
            }
        } else {
            runShitWithMaybeDB()
        }

        responseBean = if (spec.wrapInFormResponse) FormResponse.Hunky(res) else res

    } catch (e: ExpectedRPCShit) {
        if (spec.wrapInFormResponse) {
            log.info("Softened RPC shit: ${e.message}")
            responseBean = FormResponse.Shitty(e.message, ctx.fieldErrors)
        } else {
            throw e
        }
    }

    with (servletResponse) {
        addHeader("Access-Control-Allow-Origin", "*")
        contentType = "application/json; charset=utf-8"
        writer.println(hackyObjectMapper.writeValueAsString(responseBean))
        status = HttpServletResponse.SC_OK
    }
}

fun <Req> void(shit: (ProcedureContext, Req) -> Unit): (ProcedureContext, Req) -> GenericResponse = {ctx, req ->
    shit(ctx, req)
    GenericResponse()
}

fun <Req : RequestMatumba, Res : Any>
publicProcedure(req: Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null, validate: ((ProcedureContext, Req) -> Unit)? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        validate = validate ?: {ctx, req ->},
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = false,
        userKinds = setOf(),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : Any>
anyUserProcedure(req: Req, runShit: (ProcedureContext, Req) -> Res, wrapInFormResponse: Boolean? = null): (HttpServletRequest, HttpServletResponse) -> Unit  =
    remoteProcedure(ProcedureSpec(
        req,
        runShit = runShit,
        wrapInFormResponse = wrapInFormResponse ?: true,
        needsDB = true,
        needsDangerousToken = false,
        needsUser = true,
        userKinds = setOf(UserKind.CUSTOMER, UserKind.WRITER, UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))

fun <Req : RequestMatumba, Res : Any>
adminProcedure(
    req: Req,
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
        needsUser = true,
        userKinds = setOf(UserKind.ADMIN),
        considerNextRequestTimestampFiddling = true,
        logRequestJSON = true))






















