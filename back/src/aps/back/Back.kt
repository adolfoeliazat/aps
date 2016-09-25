/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import aps.back.generated.jooq.Tables.USER_TOKENS
import aps.back.generated.jooq.tables.pojos.Users
import co.paralleluniverse.fibers.FiberAsync
import co.paralleluniverse.fibers.Suspendable
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.concurrent.thread
import kotlin.reflect.memberProperties

val objectMapper = ObjectMapper()

fun t(en: String, ru: String) = ru

fun bitchExpectedly(msg: String) {
    throw ExpectedRPCShit(msg)
}

class ExpectedRPCShit(msg: String) : Throwable(msg)

abstract class RemoteProcedure {
    val req = mutableMapOf<String, String>()
    lateinit var jooq: DSLContext
}

fun Timestamp?.toMaybePortable(): PortableTimestamp? = this?.let {it.toPortable()}

fun Timestamp.toPortable(): PortableTimestamp =
    PortableTimestamp(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(this))

fun String.toUserKind(): UserKind = UserKind.values().find{it.inDB == this} ?: wtf("[$this] to UserKind")
fun String.toLanguage(): Language = Language.values().find{it.inDB == this} ?: wtf("[$this] to Language")
fun String.toUserState(): UserState = UserState.values().find{it.inDB == this} ?: wtf("[$this] to UserState")

fun Users.toTO(): UserTO {
    return UserTO(
        id = "" + id,
        deleted = deleted,
        insertedAt = insertedAt.toPortable(),
        updatedAt = updatedAt.toPortable(),
        profileUpdatedAt = profileUpdatedAt.toMaybePortable(),
        kind = kind.toUserKind(),
        lang = lang.toLanguage(),
        email = email,
        state = state.toUserState(),
        profileRejectionReason = profileRejectionReason,
        banReason = banReason,
        adminNotes = adminNotes,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        compactPhone = compactPhone,
        aboutMe = aboutMe
    )
}

class SignInWithPasswordRemoteProcedure : RemoteProcedure() {
    val log by logger()

    @Suspendable
    fun invoke(req: SignInWithPasswordRequest, res: SignInWithPasswordResponse) {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4

        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")

        val users = jooq.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
        if (users.isEmpty()) bitchExpectedly(vagueMessage)

        val user = users[0]
        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)

        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157

        res.token = "" + UUID.randomUUID()
        jooq.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
            .values(user.id, res.token)

        // TODO:vgrechka Load related user shit?

        res.user = user.toTO()
    }
}


class HiRemoteProcedure {
    fun invoke(req: HiRequest, res: HiResponse) {
        res.saying = if (req.name != null)
            "Expecting hi? Fuck you, ${req.name}, OK?.."
        else
            "I need your name, motherfucker"

        res.backendInstance = "dev-1"
    }
}

fun fuckingTimeout(ms: Long, cb: () -> Unit) {
    thread {
        Thread.sleep(ms)
        cb()
    }
}

//class FuckingTimeoutAsync : FiberAsync<Unit, Throwable>

@Suspendable
fun fuckingDelay(ms: Long) {
    object : FiberAsync<Unit, Throwable>() {
        override fun requestAsync() {
            fuckingTimeout(ms) {
                asyncCompleted(Unit)
            }
        }

    }.run()
}

fun Logger.section(msg: Any?) {
    val line = "-".repeat(72)
    this.info("\n$line\n$msg\n$line")
}

fun Logger.striking(msg: Any?) {
    val line = "-".repeat(10)
    this.info("$line $msg")
}

fun jsonny(vararg pairs: Pair<String, Any?>) = mapOf(*pairs)

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        try {
            servletResponse.addHeader("Access-Control-Allow-Origin", "*")

            val pathInfo = servletRequest.pathInfo
            when {
                pathInfo.startsWith("/rpc/") -> handleRPC(servletRequest, servletResponse)

                pathInfo == "/meta" -> {
                    // {classes: [{name: '', fields: [{name: '', strategy: {type: 'Enum', ...}}]}]}
                    fun serializeSerializationStrategy_niceName_huh(t: Type): Map<String, Any?> = when {
                        t.oneOf(String::class.java, Integer::class.java, Boolean::class.java) -> jsonny(
                            "type" to "Simple")
                        t is ParameterizedType -> {
                            val rt = t.rawType
                            when {
                                rt is Class<*> && rt.isAssignableFrom(List::class.java) -> jsonny(
                                    "type" to "List",
                                    "itemStrategy" to serializeSerializationStrategy_niceName_huh(t.actualTypeArguments.first()))
                                else -> wtf("Parametrized type $t")
                            }
                        }
                        t is Class<*> -> when {
                            t.isEnum -> jsonny(
                                "type" to "Enum",
                                "enumClassName" to t.name)
                            t.`package`.name == "aps" -> jsonny(
                                "type" to "Class",
                                "className" to t.name)
                            else -> wtf("Class $t")
                        }
                        else -> wtf("Type $t")
                    }

                    val serializableClasses = listOf(
                        SignInWithPasswordResponse::class, UserTO::class, PortableTimestamp::class)

                    objectMapper.writeValue(servletResponse.writer, jsonny(
                        "classes" to serializableClasses.map{clazz -> jsonny(
                            "name" to (clazz.qualifiedName ?: wtf("Class without a qualifiedName")),
                            "fields" to clazz.memberProperties.map{prop ->
                                val getterMethod = clazz.java.getMethod("get${prop.name.capitalize()}")
                                val propClass = getterMethod.returnType
                                // log.striking(prop.name + ": " + propClass.name)
                                jsonny(
                                    "name" to prop.name,
                                    "strategy" to serializeSerializationStrategy_niceName_huh(getterMethod.genericReturnType)
                                )
                            }
                        )}
                    ))
                }

                else -> bitch("Weird request path: $pathInfo")
            }
        } catch(e: Throwable) {
            throw ServletException(e)
        }
    }

    @Suspendable
    private fun handleRPC(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
        val cnamePrefix = procedureName.capitalize()

        val procedureClass = Class.forName("aps.back.${cnamePrefix}RemoteProcedure")
        val requestClass = Class.forName("aps.${cnamePrefix}Request")
        val responseClass = Class.forName("aps.${cnamePrefix}Response")

        val requestJSON = servletRequest.reader.readText()
        log.info("requestJSON: $requestJSON")
        val request = objectMapper.readValue(requestJSON, requestClass)

        servletResponse.contentType = "application/json; charset=utf-8"
        servletResponse.status = HttpServletResponse.SC_OK

        val procedure = procedureClass.newInstance() as RemoteProcedure
        val response = responseClass.newInstance() as RemoteProcedureResponse

        try {
            val connection = DS.apsTestOnTestServer.connection
            try {
                procedure.jooq = DSL.using(connection, SQLDialect.POSTGRES_9_5)

                val method = procedure.javaClass.getMethod("invoke", requestClass, responseClass)
                method.invoke(procedure, request, response)
            } finally {
                connection.close()
            }
        } catch (e: ExpectedRPCShit) {
            log.warn("Expected RPC shit: ${e.message}", e)
            response.error = e.message
        } catch (e: Throwable) {
            log.error("RPC fuckup: ${e.message}", e)
            response.error = t("Service is temporarily fucked up, sorry", "Сервис временно в жопе, просим прощения")
        }

        val json = objectMapper.writeValueAsString(response)

        servletResponse.writer.println(json)
    }

}

fun main(args: Array<String>) {
    val server = Server(8080)
    val handler = ServletHandler()
    server.handler = handler
    handler.addServletWithMapping(GodServlet::class.java, "/*")
    server.start()

    println("APS backend shit is spinning...")
    server.join()
}



