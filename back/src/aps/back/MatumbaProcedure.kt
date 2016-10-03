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
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MatumbaProcedure<Req : RequestMatumba, Res : Any>(val req: Req, val res: Res, build: MatumbaProcedure<Req, Res>.() -> Unit) {
    enum class Access { SYSTEM, USER, PUBLIC }

    val log by logger()

    lateinit var access: Access
    var softenShit = true
    var needsDBConnection = true
    var validate: () -> Unit = {}
    lateinit var runShit: () -> Unit

    init {
        build()
    }

    lateinit var lang: Language
    lateinit var clientKind: ClientKind
    lateinit var q: DSLContext
    lateinit var user: UserRTO
    lateinit var requestTimestamp: Timestamp
    lateinit var clientDomain: String
    lateinit var clientPortSuffix: String
    val fieldErrors = mutableListOf<FieldError>()

    fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val responseBean: Any
        try {
            servletRequest.characterEncoding = "UTF-8"
            val requestJSON = servletRequest.reader.readText()
            log.info("${servletRequest.pathInfo}: $requestJSON")
            val rmap = hackyObjectMapper.readValue(requestJSON, Map::class.java)
            log.section("rmap:", rmap)

            clientKind = ClientKind.valueOf(rmap["clientKind"] as String)
            lang = Language.valueOf(rmap["lang"] as String)

            requestTimestamp = TestServerFiddling.nextRequestTimestamp?.let {
                TestServerFiddling.nextRequestTimestamp = null
                it
            } ?: Timestamp(Date().time)

            if (access != Access.SYSTEM) {
                when (lang) {
                    Language.EN -> when (clientKind) {
                        ClientKind.CUSTOMER -> {clientDomain = "aps-en-customer.local"; clientPortSuffix = ":3011"}
                        ClientKind.WRITER -> {clientDomain = "aps-en-writer.local"; clientPortSuffix = ":3021"}
                    }
                    Language.UA -> when (clientKind) {
                        ClientKind.CUSTOMER -> {clientDomain = "aps-ua-customer.local"; clientPortSuffix = ":3012"}
                        ClientKind.WRITER -> {clientDomain = "aps-ua-writer.local"; clientPortSuffix = ":3022"}
                    }
                }
            }

            if (access == Access.SYSTEM) {
                // TODO:vgrechka Check DANGEROUS_TOKEN    50ec0187-3b47-43de-8a29-b561e6d7132f
            }

            // TODO:vgrechka This is not pretty
            if (access == Access.USER && !needsDBConnection) wtf("USER access assumes DB connection")

            if (!needsDBConnection) {
                runShit()
            } else {
                val db = DB.apsTestOnTestServer
                db.joo {
                    // TODO:vgrechka Wrap each RPC in transaction    5928def7-392e-433f-99a8-9decfe959971
                    q = it

                    if (access == Access.USER) {
                        val token = rmap["token"] as String
                        val rows = q.select()
                            .from(USER_TOKENS, USERS)
                            .where(USER_TOKENS.TOKEN.eq(token))
                            .and(USERS.ID.eq(USER_TOKENS.USER_ID))
                            .fetch().into(Users::class.java)
                        if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2

                        // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1

                        user = rows[0].toRTO(q)
                    }

                    val input  = rmap["fields"] as Map<String, Any?>
                    for (field in req.fields) field.load(input, fieldErrors)
                    validate()
                    if (fieldErrors.isNotEmpty()) bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))

                    runShit()
                }
            }

            responseBean = if (softenShit) FormResponse.Hunky(res) else res

        } catch (e: ExpectedRPCShit) {
            if (softenShit) {
                log.info("Softened RPC shit: ${e.message}")
                responseBean = FormResponse.Shitty(e.message, fieldErrors)
            } else {
                throw e
            }
        }

        servletResponse.addHeader("Access-Control-Allow-Origin", "*")
        servletResponse.contentType = "application/json; charset=utf-8"
        servletResponse.writer.println(hackyObjectMapper.writeValueAsString(responseBean))
        servletResponse.status = HttpServletResponse.SC_OK
    }

}

