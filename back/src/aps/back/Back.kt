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
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.databind.deser.ValueInstantiator
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.*
import com.fasterxml.jackson.databind.type.SimpleType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.LinkedNode
import com.google.common.base.CaseFormat
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

val THE_ADMIN_ID = 101L // TODO:vgrechka Unhardcode admin ID    17c5cc52-57c2-480d-a7c3-abb030b01cc9

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

val hackyObjectMapper = ObjectMapper().applet {om ->
    om.serializerFactory = object:BeanSerializerFactory(null) {
        override fun findBeanProperties(prov: SerializerProvider,
                                        beanDesc: BeanDescription,
                                        builder: BeanSerializerBuilder): List<BeanPropertyWriter>? {

            open class DumbBeanPropertyWriter : BeanPropertyWriter() {
                override fun fixAccess(config: SerializationConfig?) {
                    // At least not NPE
                }

                override fun getSerializationType(): JavaType {
                    // At least not NPE
                    return TypeFactory.unknownType()
                }
            }

            val writers = super.findBeanProperties(prov, beanDesc, builder) ?: mutableListOf<BeanPropertyWriter>()

            writers.forEachIndexed {i, w ->
                if (w.type.isEnumType) {
                    writers[i] = object:DumbBeanPropertyWriter() {
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

            writers.add(0, object:DumbBeanPropertyWriter() {
                override fun serializeAsField(bean: Any, gen: JsonGenerator, prov: SerializerProvider) {
                    gen.writeStringField("\$\$\$class", bean.javaClass.name)
                }
            })

            return writers
        }
    }

//    om.addHandler(object:DeserializationProblemHandler() {
//        override fun handleMissingInstantiator(ctxt: DeserializationContext?, instClass: Class<*>, p: JsonParser?, msg: String?): Any {
//            if (Request::class.java.isAssignableFrom(instClass)) {
//                val ctor = instClass.declaredConstructors.first()
//                val dummyArgs = ctor.parameters.map {p-> when (p.type) {
//                    java.lang.Boolean.TYPE -> false
//                    java.lang.Integer.TYPE -> 0
//                    java.lang.Long.TYPE -> 0L
//                    String::class.java -> "--SHIT--"
//                    else -> null
//                }}
//                return ctor.newInstance(*dummyArgs.toTypedArray())
//            }
//            return super.handleMissingInstantiator(ctxt, instClass, p, msg)
//        }
//    })


//    om.registerModule(SimpleModule().applet{m->
//        m.setDeserializers(object:SimpleDeserializers() {
//            override fun findBeanDeserializer(type: JavaType, config: DeserializationConfig?, beanDesc: BeanDescription?): JsonDeserializer<*>? {
//                if (CoolField::class.java.isAssignableFrom(type.rawClass)) {
//                    return object:StdDeserializer<CoolField>(CoolField::class.java) {
//                        override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): CoolField {
//                            if (p.currentToken != JsonToken.VALUE_STRING) bitch("I want a freakin' string for [${p.currentName}]")
//                            return (type.rawClass.newInstance() as CoolField).applet{f->
//                                f.inputString = p.text
//                            }
//                        }
//                    }
//                }
//
//                return null
//            }
//        })
//    })

}

fun jsonize(obj: Any?): String {
    return hackyObjectMapper.writeValueAsString(obj)
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

object ImposedShit {
    @Volatile
    var requestTimestamp: Timestamp? = null
}


class ImposeNextRequestTimestampRemoteProcedure : RemoteProcedure<ImposeNextRequestTimestampRequest, GenericResponse>() {
    override val access: Access = Access.SYSTEM

    override fun doStuff() {
        ImposedShit.requestTimestamp = stringToStamp(req.stamp)
    }
}

class ResetTestDatabaseRemoteProcedure : RemoteProcedure<ResetTestDatabaseRequest, ResetTestDatabaseResponse>() {
    override val access: Access = Access.SYSTEM
    override val needsDBConnection = false // Otherwise we can't use DB as template for cloning

    val log by logger()

    override fun doStuff() {
        val templateDB = DB.byNameOnTestServer(req.templateDB)

        if (req.recreateTemplate) {
            templateDB.recreate()
        }

        DB.apsTestOnTestServer.recreate(template = templateDB)
    }
}

fun Logger.section(vararg msgs: Any?) {
    val line = "-".repeat(72)
    this.info("\n$line\n" + msgs.joinToString(" ") + "\n$line")
}

fun Logger.striking(msg: Any?) {
    val line = "-".repeat(10)
    this.info("$line $msg")
}

abstract class RemoteProcedure<Req: Request, Res: Any> {
    enum class Access { SYSTEM, USER, PUBLIC }

    abstract val access: Access
    abstract fun doStuff()

    open val needsDBConnection = true

    lateinit var req: Req
    lateinit var res: Res
    lateinit var q: DSLContext
    lateinit var user: Users
    val requestTimestamp: Timestamp

    val fields = mutableListOf<SomeField>()
    val fieldErrors = mutableListOf<FieldError>()

    init {
        requestTimestamp = ImposedShit.requestTimestamp?.let {
            ImposedShit.requestTimestamp = null
            it
        } ?: Timestamp(Date().time)
    }

    fun invoke() {
        val req = this.req

        if (access == Access.SYSTEM) {
            // TODO:vgrechka Check DANGEROUS_TOKEN    50ec0187-3b47-43de-8a29-b561e6d7132f
        }

        if (access == Access.USER) {
            val rows = q.select()
                .from(USER_TOKENS, USERS)
                .where(USER_TOKENS.TOKEN.eq(req.token))
                .and(USERS.ID.eq(USER_TOKENS.USER_ID))
                .fetch().into(Users::class.java)
            if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2

            // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1

            user = rows[0]
        }

        for (field in fields) {
            field.load()
        }

        if (!fieldErrors.isEmpty()) bitchExpectedly(t("TOTE", "Пожалуйста, исправьте ошибки ниже"))

        doStuff()
    }

    abstract class SomeField {
        abstract fun load()
    }

    abstract class StringValueField : SomeField() {
        abstract val value: String
    }

    inner open class CrappyField(val name: String) : StringValueField() {
        override lateinit var value: String

        init {
            fields.add(this)
        }

        override fun load() {
            value = req.fields[name] ?: bitch("Gimme $name, motherfucker")
        }
    }

    fun emailField(name: String = "email"): StringValueField {
        return object:CrappyField(name) {
        }
    }

    fun passwordField(name: String = "password"): StringValueField {
        return object:CrappyField(name) {
        }
    }

//    inner class PeggyField(name: String) : CrappyField(name) {
//        override fun load() {
//            value = req.fields[name] ?: bitch("Gimme $name, motherfucker")
//            value = value.trim()
//
//            run error@{
//                if (value.length < minLen) {
//                    if (value.length == 0) {
//                        return@error "Поле обязательно"
//                    }
//                }
//                null
//            }?.let {error ->
//                fieldErrors.add(FieldError(name, error)); return
//            }
//        }
//    }

    fun textField(name: String, minLen: Int, maxLen: Int): StringValueField {
        return object:CrappyField(name) {
            override fun load() {
                value = req.fields[name] ?: bitch("Gimme $name, motherfucker")
                value = value.trim()

                run error@{
                    if (value.length < minLen)
                        return@error if (value.length == 0) t("TOTE", "Поле обязательно")
                                     else t("TOTE", "Не менее $minLen символов")
                    if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")
                    null
                }?.let {error ->
                    fieldErrors.add(FieldError(name, error)); return
                }
            }
        }
    }

    fun phoneField(name: String = "phone", minDigitCount: Int = 6, maxLen: Int = 20): StringValueField {
        return object:CrappyField(name) {
            override fun load() {
                value = req.fields[name] ?: bitch("Gimme $name, motherfucker")
                value = value.trim()

                run error@{
                    if (value.length == 0) return@error t("TOTE", "Поле обязательно")
                    if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")

                    var digitCount = 0
                    for (c in value.toCharArray()) {
                        if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return@error t("TOTE", "Странный телефон какой-то")
                        if (Regex("\\d").matches("$c")) ++digitCount
                    }

                    if (digitCount < minDigitCount) return@error t("TOTE", "Не менее $minDigitCount цифр")

                    null
                }?.let {error ->
                    fieldErrors.add(FieldError(name, error)); return
                }
            }
        }
    }

}

class SignInWithPasswordRemoteProcedure : RemoteProcedure<Request, SignInWithPasswordResponse>() {
    override val access: Access = Access.PUBLIC

    val email = emailField()
    val password = passwordField()

    override fun doStuff() {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4

        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")

        val users = q.select().from(USERS).where(USERS.EMAIL.equal(email.value)).fetch().into(Users::class.java)
        if (users.isEmpty()) bitchExpectedly(vagueMessage)

        val user = users[0]
        if (!BCrypt.checkpw(password.value, user.passwordHash)) bitchExpectedly(vagueMessage)

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
                    val requestClass =
                        try {
                            Class.forName("aps.back.${cnamePrefix}Request")
                        } catch(e: ClassNotFoundException) {
                            try {
                                Class.forName("aps.${cnamePrefix}Request")
                            } catch(e: ClassNotFoundException) {
                                Request::class.java
                            }
                        }
                    val responseClass =
                        try {
                            Class.forName("aps.${cnamePrefix}Response")
                        } catch(e: ClassNotFoundException) {
                            GenericResponse::class.java
                        }

                    val procedure = procedureClass.newInstance() as RemoteProcedure<Request, Any>

                    val requestJSON = servletRequest.reader.readText()
                    log.info("requestJSON: $requestJSON")
                    procedure.req = hackyObjectMapper.readValue(requestJSON, requestClass) as Request

                    procedure.res = responseClass.newInstance()

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

                    servletResponse.writer.println(hackyObjectMapper.writeValueAsString(response))
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


//class TextFieldBack(val proc: RemoteProcedure<*, *>, val name: String, val minLen: Int, val maxLen: Int) : FieldBack() {
//    init {
//        proc.fields.add(this)
//    }
//}

//class CoolField(proc: RemoteProcedure<*, *>, val kind: Kind, val name: String = defaultName(kind), build: (CoolField.() -> Unit)? = null) : FieldBack() {
//    enum class Kind { PHONE }
//
//    lateinit var inputString: String
//    var mandatory: Boolean = true
//    var maxLen: Int = -1
//
//    init {
//        proc.fields.add(this)
//        build?.let {it()}
//    }
//
//    companion object {
//        fun defaultName(kind: Kind) = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, kind.name)
//    }
//
//}

//class UpdateProfileRequest : RequestBack(), ProfileFields {
//    val phone = CoolField(this, PHONE)
//
//    //    override lateinit var phone: String
//    override lateinit var aboutMe: String
//}

//class CoolRemoteProcedure {
//
//}

fun compactPhone(s: String): String {
    return s.replace(Regex("[^0-9]"), "")
}


class UpdateProfileRemoteProcedure() : RemoteProcedure<Request, UpdateProfileResponse>() {
    override val access: Access = Access.USER
    val log by logger()

    val phone = phoneField()
    val aboutMe = textField("aboutMe", 1, 300)

    override fun doStuff() {
        q.update(USERS)
            .set(USERS.PROFILE_UPDATED_AT, requestTimestamp)
            .set(USERS.PHONE, phone.value)
            .set(USERS.COMPACT_PHONE, compactPhone(phone.value))
            .set(USERS.ABOUT_ME, aboutMe.value)
            .set(USERS.STATE, UserState.PROFILE_APPROVAL_PENDING.name)
            .set(USERS.ASSIGNED_TO, THE_ADMIN_ID)
            .where(USERS.ID.eq(user.id))
            .execute()

        val users = q.select().from(USERS).where(USERS.ID.eq(user.id)).fetch().into(Users::class.java)
        res.newUser = users.first().toRTO()

//        loadField(s{key: 'phone', kind: 'phone', mandatory: true})
//        loadField(s{key: 'aboutMe', mandatory: true, maxlen: 300})


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


















