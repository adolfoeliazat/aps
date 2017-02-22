package aps.front

import aps.*
import into.kommon.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun myName() = object:ReadOnlyProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String =
        property.name
}

fun <T> named(make: (ident: String) -> T) = object:ReadOnlyProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null)
            value = make(property.name)
        return bang(value)
    }

}

class eagerNamed<out T>(val make: (ident: String) -> T) {
    operator fun provideDelegate(thiz: Any?, property: KProperty<*>) = run {
        val value = make(property.name)
        object:ReadOnlyProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
        }
    }
}

external fun downloadjs(data: Any, fileName: String, mime: String)

external fun decodeURI(encodedURI: String): String

interface URLQueryParamsMarker

fun disableableHandler(disabled: Boolean, f: suspend () -> Unit) = when {
    disabled -> {
        {TestGlobal.disabledActionHitLock.resumeTestFromSut()}
    }
    else -> f
}

fun isAdmin() = Globus.world.userMaybe?.kind == UserKind.ADMIN
fun isWriter() = Globus.world.userMaybe?.kind == UserKind.WRITER
fun isCustomer() = Globus.world.userMaybe?.kind == UserKind.CUSTOMER
fun user() = Globus.world.user

fun populateWithAdminNotes(o: RequestWithAdminNotes, rto: RTOWithAdminNotes) {
    if (isAdmin()) {
        o.adminNotes.value = rto.adminNotes
    }
}

fun renderAdminNotesIfNeeded(o: ElementBuilder, rto: RTOWithAdminNotes) {
    if (isAdmin() && rto.adminNotes.isNotBlank()) {
        o - MelindaTools.detailsRow(rto.adminNotes, rto.adminNotesHighlightRanges, title = fields.adminNotes.title)
    }
}

fun <T : RequestMatumba> T.populateCheckingCompleteness(block: (T) -> Unit): T {
    // TODO:vgrechka Also check for excessiveness?
    Globus.populatedFields.clear()
    block(this)
    for (field in this._fields + this._hiddenFields) {
        if (field.include && field !in Globus.populatedFields)
            bitch("Field ${field.name} of ${this::class.simpleName} should be populated")
    }
    return this
}























