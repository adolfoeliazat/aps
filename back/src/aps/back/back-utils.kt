/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

fun Throwable.stackString(): String {
    val sw = StringWriter()
    PrintWriter(sw).use {this.printStackTrace(it)}
    return sw.toString()
}

inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Throwable) {
        closed = true
        if (this != null) {
            try {
                this.close()
            } catch (closeException: Throwable) {
                e.addSuppressed(closeException)
            }
        }
        throw e
    } finally {
        if (this != null && !closed) {
            close()
        }
    }
}

fun eprintln(msg: String = "") {
    System.err.println(msg)
}

inline fun dwarnStriking(vararg xs: Any?) = dwarn("\n\n", "**********", *xs, "\n")

fun <T: Any> volatileNotNull(): ReadWriteProperty<Any?, T> = VolatileNotNullVar()

private class VolatileNotNullVar<T: Any>() : ReadWriteProperty<Any?, T> {
    private @Volatile var value: T? = null

    public override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    public override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun generateUserToken() =
    TestServerFiddling.nextGeneratedUserToken.getAndReset()
    ?: UUID.randomUUID().toString()

fun generatePassword() =
    TestServerFiddling.nextGeneratedPassword.getAndReset()
    ?: UUID.randomUUID().toString()

fun hashPassword(clearText: String): String = BCrypt.hashpw(clearText, BCrypt.gensalt())



fun isAdmin() = requestUserMaybe?.user?.kind == UserKind.ADMIN

fun adminNotesForCreate(req: RequestWithAdminNotes) = when {
    isAdmin() -> req.adminNotes.value
    else -> ""
}

fun updateAdminNotes(fields: FieldsWithAdminNotes, req: RequestWithAdminNotes) {
    if (isAdmin())
        fields.adminNotes = req.adminNotes.value
}

fun <T> checkingAllFieldsRetrieved(req: RequestMatumba, block: () -> T): T {
    backPlatform.requestGlobus.retrievedFields.clear()
    val res = block()
    for (field in req._fields) {
        if (field.include && field !in backPlatform.requestGlobus.retrievedFields)
            bitch("Field ${field.name} of ${req::class.simpleName} should be retrieved")
    }
    return res
}












