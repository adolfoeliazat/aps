/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.UserRoles
import aps.back.generated.jooq.tables.pojos.Users
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.ser.*
import com.fasterxml.jackson.databind.type.TypeFactory
import org.apache.commons.validator.routines.EmailValidator
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.jooq.DSLContext
import org.mindrot.jbcrypt.BCrypt
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.slf4j.Logger
import java.lang.reflect.Method
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.Servlet
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

val THE_ADMIN_ID = 101L // TODO:vgrechka Unhardcode admin ID    17c5cc52-57c2-480d-a7c3-abb030b01cc9

val remoteProcedureNameToFactory: MutableMap<String, Method> = Collections.synchronizedMap(mutableMapOf())

fun main(args: Array<String>) {
    // System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

    run { // Gather meta
        val refl = Reflections(ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("aps.back"))
            .setScanners(MethodAnnotationsScanner()))
        val methods = refl.getMethodsAnnotatedWith(RemoteProcedureFactory::class.java)
        debugLog.section("Remote procedure factories:", methods.map{it.name}.joinToString())
        for (m in methods) remoteProcedureNameToFactory[m.name] = m
    }

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
}

fun t(en: String, ua: String) = ua

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

fun Users.toRTO(q: DSLContext): UserRTO {
    val roles = q.select().from(USER_ROLES).where(USER_ROLES.USER_ID.eq(id)).fetchInto(UserRoles::class.java)

    // TODO:vgrechka Double-check all secrets are excluded from UserRTO    7c2d1191-d43b-485c-af67-b95b46bbf62b
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
        aboutMe = aboutMe,
        roles = roles.map{UserRole.valueOf(it.role)}.toSet()
    )
}


//class ResetTestDatabaseRemoteProcedure : RemoteProcedure<ResetTestDatabaseRequest, GenericResponse>() {
//    override val access: Access = Access.SYSTEM
//    override val needsDBConnection = false // Otherwise we can't use DB as template for cloning
//
//    val log by logger()
//
//    override fun doStuff() {
//        val templateDB = DB.byNameOnTestServer(req.templateDB)
//
//        if (req.recreateTemplate) {
//            templateDB.recreate()
//        }
//
//        DB.apsTestOnTestServer.recreate(template = templateDB)
//    }
//}

fun Logger.section(vararg msgs: Any?) {
    val line = "-".repeat(72)
    this.info("\n$line\n" + msgs.joinToString(" ") + "\n$line")
}

fun Logger.striking(msg: Any?) {
    val line = "-".repeat(10)
    this.info("$line $msg")
}

//abstract class RemoteProcedure<Req: Request, Res: Any> {
//    enum class Access { SYSTEM, USER, PUBLIC }
//
//    abstract val access: Access
//    abstract fun doStuff()
//
//    open val needsDBConnection = true
//
//    lateinit var req: Req
//    lateinit var res: Res
//    lateinit var q: DSLContext
//    lateinit var user: UserRTO
//    lateinit var requestTimestamp: Timestamp
//    lateinit var clientDomain: String
//    lateinit var clientPortSuffix: String
//
//    val fields = mutableListOf<Any>()
//    val fieldErrors = mutableListOf<FieldError>()
//
//    private fun clientKindDescr(): String {
//        throw UnsupportedOperationException("Implement me, please, fuck you")
//    }
//
////    fun bitchAboutFieldErrors() {
////        bitchExpectedly(t("Please fix errors below", "Пожалуйста, исправьте ошибки ниже"))
////    }
//
//    init {
//    }
//
//    fun invoke() {
//        val req = this.req
//
//        requestTimestamp = TestServerFiddling.nextRequestTimestamp?.let {
//            TestServerFiddling.nextRequestTimestamp = null
//            it
//        } ?: Timestamp(Date().time)
//
//        if (access != Access.SYSTEM) {
//            when (req.lang) {
//                Language.EN -> when (req.clientKind) {
//                    ClientKind.CUSTOMER -> {clientDomain = "aps-en-customer.local"; clientPortSuffix = ":3011"}
//                    ClientKind.WRITER -> {clientDomain = "aps-en-writer.local"; clientPortSuffix = ":3021"}
//                }
//                Language.UA -> when (req.clientKind) {
//                    ClientKind.CUSTOMER -> {clientDomain = "aps-ua-customer.local"; clientPortSuffix = ":3012"}
//                    ClientKind.WRITER -> {clientDomain = "aps-ua-writer.local"; clientPortSuffix = ":3022"}
//                }
//            }
//        }
//
//        if (access == Access.SYSTEM) {
//            // TODO:vgrechka Check DANGEROUS_TOKEN    50ec0187-3b47-43de-8a29-b561e6d7132f
//        }
//
////        if (access == Access.USER) {
////            val rows = q.select()
////                .from(USER_TOKENS, USERS)
////                .where(USER_TOKENS.TOKEN.eq(req.token))
////                .and(USERS.ID.eq(USER_TOKENS.USER_ID))
////                .fetch().into(Users::class.java)
////            if (rows.isEmpty()) bitch("Invalid token") // TODO:vgrechka Redirect user to sign-in page    301a55be-8bb4-4c60-ae7b-a6201f17d8e2
////
////            // TODO:vgrechka Check that user kind matches requesting client kind    fc937ee4-010c-4f5e-bece-5d7db51bf8c1
////
////            user = rows[0].toRTO()
////        }
//
////        for (field in fields) field.load()
////        validate()
////        if (!fieldErrors.isEmpty()) bitchExpectedly(t("TOTE", "Пожалуйста, исправьте ошибки ниже"))
//
//        doStuff()
//    }
//
//    open fun validate() {}
//
////    abstract class SomeField {
////        abstract fun load()
////    }
//
////    abstract class StringValueField : SomeField() {
////        abstract val value: String
////    }
////
////    abstract class BooleanValueField : SomeField() {
////        abstract val yes: Boolean
////        abstract val no: Boolean
////    }
//
////    inner open class CrappyStringField(val name: String) : StringValueField() {
////        override lateinit var value: String
////
////        init {
////            fields.add(this)
////        }
////
////        override fun load() {
////            value = (req.fields[name] ?: bitch("Gimme $name, motherfucker")) as String
////        }
////    }
//
////    inner open class CrappyBooleanField(val name: String) : BooleanValueField() {
////        var loaded = false
////        override var yes: Boolean = SHITB; get() = checking(loaded) {field}
////        override val no: Boolean get() = !yes
////
////        init {
////            fields.add(this)
////        }
////
////        override fun load() {
////            yes = (req.fields[name] ?: bitch("Gimme $name, motherfucker")) as Boolean
////            loaded = true
////        }
////    }
////
////    fun emailField(name: String = "email"): StringValueField {
////        return object: CrappyStringField(name) {
////            override fun load() {
////                value = (req.fields[name] ?: bitch("Gimme $name, motherfucker")) as String
////                value = value.trim()
////
////                run error@{
////                    val minLen = 3; val maxLen = 50
////
////                    if (value.length == 0) return@error t("TOTE", "Поле обязательно")
////                    if (value.length < minLen) return@error t("TOTE", "Не менее $minLen символов")
////                    if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")
////
////                    if (!EmailValidator.getInstance(false, true).isValid(value)) return@error t("TOTE", "Странная почта какая-то")
////
////                    null
////                }?.let {error ->
////                    fieldErrors.add(FieldError(name, error)); return
////                }
////            }
////        }
////    }
////
////    fun agreeTermsField(name: String = "agreeTerms"): BooleanValueField {
////        return object:CrappyBooleanField(name) {
////        }
////    }
////
////    fun passwordField(name: String = "password"): StringValueField {
////        return object: CrappyStringField(name) {
////        }
////    }
////
////    fun textField(name: String, minLen: Int, maxLen: Int): StringValueField {
////        return object: CrappyStringField(name) {
////            override fun load() {
////                value = (req.fields[name] ?: bitch("Gimme $name, motherfucker")) as String
////                value = value.trim()
////
////                run error@{
////                    if (value.length < minLen)
////                        return@error if (value.length == 0) t("TOTE", "Поле обязательно")
////                                     else t("TOTE", "Не менее $minLen символов")
////                    if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")
////                    null
////                }?.let {error ->
////                    fieldErrors.add(FieldError(name, error)); return
////                }
////            }
////        }
////    }
////
////    fun phoneField(name: String = "phone", minDigitCount: Int = 6, maxLen: Int = 20): StringValueField {
////        return object: CrappyStringField(name) {
////            override fun load() {
////                value = (req.fields[name] ?: bitch("Gimme $name, motherfucker")) as String
////                value = value.trim()
////
////                run error@{
////                    if (value.length == 0) return@error t("TOTE", "Поле обязательно")
////                    if (value.length > maxLen) return@error t("TOTE", "Не более $maxLen символов")
////
////                    var digitCount = 0
////                    for (c in value.toCharArray()) {
////                        if (!Regex("(\\d| |-|\\+|\\(|\\))+").matches("$c")) return@error t("TOTE", "Странный телефон какой-то")
////                        if (Regex("\\d").matches("$c")) ++digitCount
////                    }
////
////                    if (digitCount < minDigitCount) return@error t("TOTE", "Не менее $minDigitCount цифр")
////
////                    null
////                }?.let {error ->
////                    fieldErrors.add(FieldError(name, error)); return
////                }
////            }
////        }
////    }
//
//}


class GodServlet : HttpServlet() {
    val log by logger()

    override fun service(servletRequest: HttpServletRequest, servletResponse: HttpServletResponse) {
        if (TestServerFiddling.rejectAllRequests) bitch("Fuck you. I mean nothing personal, I do this to everyone...")

        val pathInfo = servletRequest.pathInfo
        try {
            when {
                pathInfo.startsWith("/rpc/") -> {
                    val procedureName = servletRequest.pathInfo.substring("/rpc/".length)

                    val factory = remoteProcedureNameToFactory[procedureName] ?: die("No fucking factory for procedure $procedureName")
                    val service = factory.invoke(null) as (HttpServletRequest, HttpServletResponse) -> Unit
                    service(servletRequest, servletResponse)

//                    val factory = remoteProcedureNameToFactory[procedureName]
//                    if (factory != null) {
//                        val sese = factory.invoke(null) as ServletService
//                        sese(servletRequest, servletResponse)
//                    } else {
//                        servletRequest.characterEncoding = "UTF-8"
//                        servletResponse.addHeader("Access-Control-Allow-Origin", "*")
//                        servletResponse.contentType = "application/json; charset=utf-8"
//
//                        val response: Any
//
//                        val cnamePrefix = procedureName.capitalize()
//                        val procedureClass = Class.forName("aps.back.${cnamePrefix}RemoteProcedure")
//                        val requestClass =
//                            try {
//                                Class.forName("aps.back.${cnamePrefix}Request")
//                            } catch(e: ClassNotFoundException) {
//                                try {
//                                    Class.forName("aps.${cnamePrefix}Request")
//                                } catch(e: ClassNotFoundException) {
//                                    Request::class.java
//                                }
//                            }
//                        val responseClass =
//                            try {
//                                Class.forName("aps.${cnamePrefix}Request\$Response")
//                            } catch(e: ClassNotFoundException) {
//                                GenericResponse::class.java
//                            }
//
//                        val procedure = procedureClass.newInstance() as RemoteProcedure<Request, Any>
//
//                        val requestJSON = servletRequest.reader.readText()
//                        log.info("${servletRequest.pathInfo}: $requestJSON")
//                        procedure.req = hackyObjectMapper.readValue(requestJSON, requestClass) as Request
//
//                        procedure.res = responseClass.newInstance()
//
//                        try {
//                            if (!procedure.needsDBConnection) {
//                                procedure.invoke()
//                            } else {
//                                val db = DB.apsTestOnTestServer
//                                db.joo { q ->
//                                    // TODO:vgrechka Wrap each RPC in transaction    5928def7-392e-433f-99a8-9decfe959971
//                                    procedure.q = q
//                                    procedure.invoke()
//                                }
//                            }
//
//                            response =
//                                if (procedure.access == RemoteProcedure.Access.SYSTEM) procedure.res
//                                else FormResponse.Hunky(procedure.res)
//
//                        } catch (e: ExpectedRPCShit) {
//                            log.info("Expected RPC shit: ${e.message}")
//                            response = FormResponse.Shitty(e.message, procedure.fieldErrors)
//                        }
//
//                        servletResponse.writer.println(hackyObjectMapper.writeValueAsString(response))
//                        servletResponse.status = HttpServletResponse.SC_OK
//                    }
                }


                else -> bitch("Weird request path: $pathInfo")
            }
        } catch(fuckup: Throwable) {
            log.error("Can't fucking service [$pathInfo]: ${fuckup.message}", fuckup)

            if (fuckup is WithCulprit) {
                log.section("Culprit:\n\n" + fuckup.culprit.constructionStack.joinToString("\n"){it.toString()})
            }

            throw ServletException(fuckup)
        }
    }
}

interface Culprit {
    val constructionStack: Array<StackTraceElement>
}

interface WithCulprit {
    val culprit: Culprit
}

class ExceptionWithCulprit(e: Throwable, override val culprit: Culprit): Exception(e.message, e), WithCulprit {
}

fun compactPhone(s: String): String {
    return s.replace(Regex("[^0-9]"), "")
}

annotation class RemoteProcedureFactory





















