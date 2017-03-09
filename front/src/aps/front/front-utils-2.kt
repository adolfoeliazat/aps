package aps.front

import aps.*
import into.kommon.*
import kotlin.js.Math
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

fun renderAdminNotesIfNeeded(rto: RTOWithAdminNotes, opts: RTOWithAdminNotesRenderingOptions = RTOWithAdminNotesRenderingOptions()): ToReactElementable =
    if (isAdmin() && rto.adminNotes.isNotBlank())
        MelindaTools.detailsRow(rto.adminNotes, rto.adminNotesHighlightRanges, title = fields.adminNotes.title, contentClassName = opts.outlineAdminNotes.then{css.redOutline})
    else
        NOTRE

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

fun renderWaitingBanner(c: css.WaitingBannerStyles, message: String) =
    kdiv(className = c.container){o->
        o- kdiv(className = c.message){o->
            o- ki(className = c.icon + " " + fa.hourglassHalf)
            o- message
        }
    }

fun renderMaybeRejectionReasonBanner(rejectionReason: String?): ToReactElementable {
    if (rejectionReason == null) return NOTRE
    val c = css.rejectionReasonBanner
    return kdiv(className = c.container){o->
        o- kdiv(className = c.title){o->
            o- when {
                isAdmin() -> t("TOTE", "Причина отказа (как ее видит засранец)")
                else -> t("TOTE", "Что нужно исправить")
            }
        }
        o- kdiv(className = c.body){o->
            o- rejectionReason
        }
    }
}

fun renderBanner1(msg: String): ToReactElementable {
    val c = css.banner1
    return kdiv(className = c.container){o->
        o- kdiv(className = c.message){o->
            o- ki(className = c.icon + " " + fa.chevronRight)
            o- msg
        }
    }
}

fun renderBannerCalmWarning(msg: String): ToReactElementable {
    val c = css.bannerCalmWarning
    return kdiv(className = c.container){o->
        o- kdiv(className = c.message){o->
            o- ki(className = c.icon + " " + fa.warning)
            o- msg
        }
    }
}

fun renderUserKindIconWithGap(userKind: UserKind) =
    ki(className = userKindIcon(userKind).className, marginRight = "0.5rem")

fun userKindIcon(userKind: UserKind): IconClass {
    return when (userKind) {
        UserKind.CUSTOMER -> fa.user
        UserKind.WRITER -> fa.pencil
        UserKind.ADMIN -> fa.cog
    }
}

suspend fun twoStepBlinkingSut(blinkElement: jquery.JQuery, act: suspend () -> Unit) {
    val blinker = effects2.blinkOn(blinkElement)
    TestGlobal.shitHalfwayLock.resumeTestAndPauseSutFromSut()
    act()
    blinker.unblink()
    TestGlobal.shitDoneLock.resumeTestFromSut()
}

fun renderMoney(cents: Int) = kspan{o->
    o- formatMoney(cents)
}

fun formatMoney(cents: Int): String {
    return when {
        (cents == -1) -> const.text.na
        else -> {
            check(cents % 100 == 0) {"cents = $cents    34c8b8ef-33b4-4689-8857-d38f1709551b"}
            check(Globus.lang == Language.UA) {"ae44a9b2-fd8b-4081-9132-6a2ca0d96c88"}
            (cents / 100).toString() + ",00 грн."
        }
    }
}

fun renderDurationHours(hours: Int) = kspan{o->
    o- formatDurationHours(hours)
}

fun formatDurationHours(hours: Int): String {
    return when {
        (hours == -1) -> const.text.na
        else -> {
            check(hours % 24 == 0) {"7f12dba3-33bf-402c-b35c-d51a8cda10df"}
            check(Globus.lang == Language.UA) {"0de29a21-b061-4e98-8367-9f203b92e7f3"}
            (hours / 24).toString() + " дн."
        }
    }
}

fun randomInt(minInclusive: Int, maxExclusive: Int): Int {
    return Math.floor(Math.random() * (maxExclusive - minInclusive)) + minInclusive
}

typealias SFUnit = suspend () -> Unit

fun renderBottomPageSpace() = kdiv(height = "3rem")

inline suspend fun <reified Meat> showingModalIfShittyResponse(noinline requestShit: suspend () -> FormResponse2<Meat>, noinline block: suspend (Meat) -> Unit): Boolean {
    val res = requestShit()
    return when (res) {
        is FormResponse2.Shitty<*> -> {
            openErrorModal(res.error)
            false
        }
        is FormResponse2.Hunky<*> -> {
            block(res.meat as Meat) // Checked because of `reified`
            true
        }
    }
}

interface ShitWithRenderFunction {
    val render: () -> ReactElement
}


object getStringUIDForObject {
    private val nameToUID = WeakMap<Any, String>()
    operator fun invoke(obj: Any): String = nameToUID.getOrPut(obj) {puid()}
}

fun <E> enumValuesToStringIDTimesTitleList(values: Array<E>): List<StringIDTimesTitle>
    where E : Enum<E>, E : Titled
{
    return values.map {StringIDTimesTitle(it.name, it.title)}
}

fun usualHeader(title: String): ToReactElementable =
    pageHeader0(title)

















