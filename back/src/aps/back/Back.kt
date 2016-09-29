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
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.ser.*
import com.fasterxml.jackson.databind.type.SimpleType
import com.fasterxml.jackson.databind.type.TypeFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.jooq.DSLContext
import org.mindrot.jbcrypt.BCrypt
import org.reflections.Reflections
import org.slf4j.Logger
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.memberProperties


fun main(args: Array<String>) {
    // System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

    Server(8080).apply {
        handler = ServletHandler().apply {
            addServletWithMapping(GodServlet::class.java, "/*")
        }

        start()
        println("APS backend shit is spinning...")
        join()
    }
}

val objectMapper = ObjectMapper().applet {om->
    om.serializerFactory = object:BeanSerializerFactory(null) {
        override fun findBeanProperties(prov: SerializerProvider,
                                        beanDesc: BeanDescription,
                                        builder: BeanSerializerBuilder): List<BeanPropertyWriter>? {
            val writers = super.findBeanProperties(prov, beanDesc, builder)
            if (writers != null) { // Jackson returns null for empty classes
                if (writers.size < 1) wtf("No default BeanPropertyWriters for $beanDesc")

                writers.add(0, object:BeanPropertyWriter(writers.first()) {
                    override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                        gen.writeStringField("\$\$\$class", bean.javaClass.name)
                    }
                })

                writers.forEachIndexed {i, w ->
                    if (w.type.isEnumType) {
                        writers[i] = object:BeanPropertyWriter(w) {
                            override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                                gen.writeFieldName(w.name)
                                gen.writeStartObject()
                                gen.writeStringField("\$\$\$enum", w.type.rawClass.name)
                                gen.writeStringField("value", bean.javaClass.getMethod("get${w.name.capitalize()}").invoke(bean)?.toString())
                                gen.writeEndObject()
                            }
                        }
                    }
                }
            } else {
                return listOf(object:BeanPropertyWriter() {
                    override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider?) {
                        gen.writeStringField("\$\$\$class", bean.javaClass.name)
                    }

                    override fun fixAccess(config: SerializationConfig?) {
                        // At least not NPE
                    }

                    override fun getSerializationType(): JavaType {
                        // At least not NPE
                        return TypeFactory.unknownType()
                    }
                })
            }

            return writers
        }
    }
}

fun jsonize(obj: Any?): String {
    return objectMapper.writeValueAsString(obj)
//    val map = mutableMapOf<String, Any?>()
//    return objectMapper.writeValueAsString(map)
}

fun t(en: String, ru: String) = ru

fun bitchExpectedly(msg: String) {
    throw ExpectedRPCShit(msg)
}

class ExpectedRPCShit(override val message: String) : Throwable(message)

fun Timestamp?.toMaybePortable(): TimestampRTO? = this?.let {it.toPortable()}

fun Timestamp.toPortable(): TimestampRTO =
    TimestampRTO(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(this))

fun String.toUserKind(): UserKind = UserKind.values().find{it.name == this} ?: wtf("[$this] to UserKind")
fun String.toLanguage(): Language = Language.values().find{it.name == this} ?: wtf("[$this] to Language")
fun String.toUserState(): UserState = UserState.values().find{it.name == this} ?: wtf("[$this] to UserState")

fun Users.toRTO(): UserRTO {
    return UserRTO(
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

//class SignInWithPasswordForm_Back : SignInWithPasswordForm() {
//    val log by logger()
//
//    fun execute() {
//    }
//
//    fun invoke(req: SignInWithPasswordRequest, res: SignInWithPasswordResponse) {
////        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
////
////        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
////
////        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
////        if (users.isEmpty()) bitchExpectedly(vagueMessage)
////
////        val user = users[0]
////        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)
////
////        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
////
////        res.token = "" + UUID.randomUUID()
////        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
////            .values(user.id, res.token)
////            .execute()
////
////        // TODO:vgrechka Load related user shit?
////
////        res.user = user.toTO()
//    }
//}

//class SignInWithPasswordFormBack : RemoteProcedure() {
//    val log by logger()
//
//    fun invoke(req: SignInWithPasswordForm, res: SignInWithPasswordForm.Response) {
//        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
//
//        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
//
//        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
//        if (users.isEmpty()) bitchExpectedly(vagueMessage)
//
//        val user = users[0]
//        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)
//
//        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
//
//        res.token = "" + UUID.randomUUID()
//        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
//            .values(user.id, res.token)
//            .execute()
//
//        // TODO:vgrechka Load related user shit?
//
//        res.user = user.toTO()
//    }
//}

//class SignInWithPasswordRemoteProcedure : RemoteProcedure() {
//    val log by logger()
//
//    @Suspendable
//    fun invoke(req: SignInWithPasswordRequest, res: SignInWithPasswordResponse) {
//        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
//
//        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
//
//        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
//        if (users.isEmpty()) bitchExpectedly(vagueMessage)
//
//        val user = users[0]
//        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)
//
//        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
//
//        res.token = "" + UUID.randomUUID()
//        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
//            .values(user.id, res.token)
//            .execute()
//
//        // TODO:vgrechka Load related user shit?
//
//        res.user = user.toTO()
//    }
//}

//class UpdateProfileRemoteProcedure : RemoteProcedure() {
//    val log by logger()
//
//    @Suspendable
//    fun invoke(req: SignInWithPasswordRequest, res: SignInWithPasswordResponse) {
//        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
//
//        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
//
//        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
//        if (users.isEmpty()) bitchExpectedly(vagueMessage)
//
//        val user = users[0]
//        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)
//
//        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
//
//        res.token = "" + UUID.randomUUID()
//        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
//            .values(user.id, res.token)
//            .execute()
//
//        // TODO:vgrechka Load related user shit?
//
//        res.user = user.toTO()
//    }
//}

class ResetTestDatabaseRemoteProcedure : RemoteProcedure<ResetTestDatabaseRequest, ResetTestDatabaseResponse>() {
    override val isInternal = true
    override val needsDBConnection = false // Otherwise we can't use DB as template for cloning

    val log by logger()

    override fun invoke() {
        val templateDB = DB.byNameOnTestServer(req.templateDB)

        if (req.recreateTemplate) {
            templateDB.recreate()
        }

        DB.apsTestOnTestServer.recreate(template = templateDB)
    }
}

fun Logger.section(msg: Any?) {
    val line = "-".repeat(72)
    this.info("\n$line\n$msg\n$line")
}

fun Logger.striking(msg: Any?) {
    val line = "-".repeat(10)
    this.info("$line $msg")
}

abstract class RemoteProcedure<Req: Any, Res: Any> {
    open val isInternal = false
    open val needsDBConnection = true

    lateinit var req: Req
    lateinit var res: Res
    lateinit var q: DSLContext

    val fieldErrors = mutableListOf<FieldError>()

    abstract fun invoke()
}

abstract class PrivateRemoteProcedure<Req: PrivateRequest, Res: Any> : RemoteProcedure<Req, Res>() {
    lateinit var user: Users

    abstract fun invokeSignedIn()

    override fun invoke() {
        loadRequestTokenUser()
        invokeSignedIn()
    }

    fun loadRequestTokenUser() {
        val rows = q.select()
            .from(USERS, USER_TOKENS)
            .where(USER_TOKENS.TOKEN.eq(req.token))
            .and(USERS.ID.eq(USER_TOKENS.USER_ID))
            .fetch().into(Users::class.java)
        if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2

        // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1

        user = rows[0]
    }
}

class SignInWithPasswordRemoteProcedure : RemoteProcedure<SignInWithPasswordRequest, SignInWithPasswordResponse>() {
    override fun invoke() {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4

        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")

        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email)).fetch().into(Users::class.java)
        if (users.isEmpty()) bitchExpectedly(vagueMessage)

        val user = users[0]
        if (!BCrypt.checkpw(req.password, user.passwordHash)) bitchExpectedly(vagueMessage)

        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157

        // TODO:vgrechka Store tokens in Redis instead of DB    c51fe75c-f55e-4a68-9a7b-465e44db6235
        res.token = "" + UUID.randomUUID()
        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
            .values(user.id, res.token)
            .execute()

        // TODO:vgrechka Load related user shit?

        res.user = user.toRTO()
    }
}

class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        val pathInfo = servletRequest.pathInfo
        try {
            servletResponse.addHeader("Access-Control-Allow-Origin", "*")
            servletResponse.contentType = "application/json; charset=utf-8"

            when {
                pathInfo.startsWith("/rpc/") -> {
                    // TODO:vgrechka Think about securing /rpc    ba3ffaac-6037-4991-9f51-abc8f17934f2

                    val procedureName = servletRequest.pathInfo.substring("/rpc/".length)
                    val cnamePrefix = procedureName.capitalize()

                    val procedureClass = Class.forName("aps.back.${cnamePrefix}RemoteProcedure")
                    val requestClass = Class.forName("aps.${cnamePrefix}Request")
                    val responseClass = Class.forName("aps.${cnamePrefix}Response")

                    val procedure = procedureClass.newInstance() as RemoteProcedure<Any, Any>
                    procedure.res = responseClass.newInstance()

                    val requestJSON = servletRequest.reader.readText()
                    log.info("requestJSON: $requestJSON")
                    procedure.req = objectMapper.readValue(requestJSON, requestClass)

                    if (procedure.isInternal) {
                        // TODO:vgrechka Implement DANGEROUS_TOKEN check    d1f4e0ec-792c-484c-9192-eaf0e906dd06
                    }

                    val response: FormResponse
                    try {
                        if (!procedure.needsDBConnection) {
                            procedure.invoke()
                        } else {
                            val db = DB.apsTestOnTestServer
                            db.joo { q ->
                                // TODO:vgrechka Wrap each RPC in transaction    5928def7-392e-433f-99a8-9decfe959971
                                procedure.q = q
                                procedure.invoke()
                            }
                        }
                        response = FormResponse.Hunky(procedure.res)
                    } catch (e: ExpectedRPCShit) {
                        log.info("Expected RPC shit: ${e.message}")
                        response = FormResponse.Shitty(e.message, procedure.fieldErrors)
                    }

                    servletResponse.writer.println(objectMapper.writeValueAsString(response))
                    servletResponse.status = HttpServletResponse.SC_OK
                }

//                pathInfo == "/meta" -> {
//                    // {classes: [{name: '', fields: [{name: '', strategy: {type: 'Enum', ...}}]}]}
//                    fun serializeSerializationStrategy_niceName_huh(t: Type): Map<String, Any?> = when {
//                        t.oneOf(String::class.java, Integer::class.java, Boolean::class.java) -> jsonny(
//                            "type" to "Simple")
//                        t is ParameterizedType -> {
//                            val rt = t.rawType
//                            when {
//                                rt is Class<*> && rt.isAssignableFrom(List::class.java) -> jsonny(
//                                    "type" to "List",
//                                    "itemStrategy" to serializeSerializationStrategy_niceName_huh(t.actualTypeArguments.first()))
//                                else -> wtf("Parametrized type $t")
//                            }
//                        }
//                        t is Class<*> -> when {
//                            t.isEnum -> jsonny(
//                                "type" to "Enum",
//                                "enumClassName" to t.name)
//                            t.`package`.name == "aps" -> jsonny(
//                                "type" to "Class",
//                                "className" to t.name)
//                            else -> wtf("Class $t")
//                        }
//                        else -> wtf("Type $t")
//                    }
//
//                    val serializableClasses = mutableListOf<Class<*>>()
//                    val reflections = Reflections("aps")
//                    serializableClasses.addAll(reflections
//                        .getTypesAnnotatedWith(RemoteTransferObject::class.java))
//                    serializableClasses.addAll(reflections
//                        .getSubTypesOf(RemoteProcedureResponse::class.java)
//                        .filter {!Modifier.isAbstract(it.modifiers)})
//
//                    objectMapper.writeValue(servletResponse.writer, jsonny(
//                        "classes" to serializableClasses.map{clazz ->
//                            val klazz = clazz.kotlin
//                            jsonny(
//                                "name" to (klazz.qualifiedName ?: wtf("Class without a qualifiedName")),
//                                "fields" to klazz.memberProperties.map {prop ->
//                                    val getterMethod = clazz.getMethod("get${prop.name.capitalize()}")
//                                    val propClass = getterMethod.returnType
//                                    // log.striking(prop.name + ": " + propClass.name)
//                                    jsonny(
//                                        "name" to prop.name,
//                                        "strategy" to serializeSerializationStrategy_niceName_huh(getterMethod.genericReturnType)
//                                    )
//                                }
//                            )
//                        }
//                    ))
//                }

                else -> bitch("Weird request path: $pathInfo")
            }
        } catch(fuckup: Throwable) {
            log.error("Can't fucking service [$pathInfo]: ${fuckup.message}", fuckup)
            throw ServletException(fuckup)
        }
    }
}

var imposedRequestTimestamp: Any? = null
var imposedNextIDs: Iterable<Long> = listOf()

fun resetImposed() {
    imposedRequestTimestamp = null
    imposedNextIDs = listOf()
}



class UpdateProfileRemoteProcedure : PrivateRemoteProcedure<UpdateProfileRequest, UpdateProfileResponse>() {
    override fun invokeSignedIn() {
        dlog("------------------- updating fucking profile for ${user.firstName} ----------------")
//        validateProfileFields(req)
//
//        if (isEmpty(fieldErrors)) {
//            #await tx.query(s{y: q`
//                update users set profile_updated_at = ${requestTimestamp},
//                phone = ${fields.phone},
//                compact_phone = ${compactPhone(fields.phone)},
//                about_me = ${fields.aboutMe},
//                state = 'PROFILE_APPROVAL_PENDING',
//                assigned_to = ${THE_ADMIN_ID}
//                where id = ${user.id}`})
//            #await loadUserForToken(s{})
//            return traceEndHandler(s{ret: hunkyDory({newUser: pickFromUser(s{user})})})
//        }
//
//        return traceEndHandler(s{ret: fixErrorsResult()})
    }

}

//fun validateProfileFields(req: ProfileFields) {
//
//    loadField(s{key: 'phone', kind: 'phone', mandatory: true})
//    loadField(s{key: 'aboutMe', mandatory: true, maxlen: 300})
//}


















