/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.RedisLogMessage.Separator.Type.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.JQUserRoles
import aps.back.generated.jooq.tables.pojos.JQUsers
import into.kommon.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler
import org.jooq.DSLContext
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.slf4j.Logger
import java.lang.reflect.Method
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

val THE_ADMIN_ID = 101L // TODO:vgrechka Unhardcode admin ID    17c5cc52-57c2-480d-a7c3-abb030b01cc9

val remoteProcedureNameToFactory: MutableMap<String, Method> = Collections.synchronizedMap(mutableMapOf())

fun main(args: Array<String>) {
    System.setProperty("user.timezone", "GMT")
    // System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")

    redisLog.send(RedisLogMessage.Separator()-{o->
        o.type = THICK_SEPARATOR
        o.text = "Booting fucking backend"
    })

    run { // Gather meta
        val refl = Reflections(ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("aps.back"))
            .setScanners(MethodAnnotationsScanner()))
        val methods = refl.getMethodsAnnotatedWith(RemoteProcedureFactory::class.java)
        debugLog.section("Remote procedure factories:", methods.map{it.name}.joinToString())
        for (m in methods) remoteProcedureNameToFactory[m.name] = m
    }

    val port = (System.getenv("PORT") ?: "8080").toInt()
    Server(port).apply {
        handler = ServletHandler().apply {
            addServletWithMapping(GodServlet::class.java, "/*")
        }

        start()
        println("APS backend shit is spinning...")
        join()
    }
}






fun t(en: String, ua: String) = ua

fun bitchExpectedly(msg: String): Nothing {
    throw ExpectedRPCShit(msg)
}

class ExpectedRPCShit(override val message: String) : Throwable(message)

fun Timestamp?.toMaybePortable(): TimestampRTO? = this?.let {it.toPortable()}

fun Timestamp.toPortable(): TimestampRTO =
    TimestampRTO(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(this))

fun String.toUserKind(): UserKind = UserKind.values().find{it.name == this} ?: wtf("[$this] to UserKind")
fun String.toLanguage(): Language = Language.values().find{it.name == this} ?: wtf("[$this] to Language")
fun String.toUserState(): UserState = UserState.values().find{it.name == this} ?: wtf("[$this] to UserState")



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




















