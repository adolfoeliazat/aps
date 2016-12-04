/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*

//fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)
//fun imf(what: String = "me"): Nothing = throw JSException("Implement $what, please, fuck you")
//fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw JSException("WTF: $msg")
//fun die(msg: String = "You killed me, motherfucker..."): Nothing = throw JSException(msg)

inline fun clog(vararg xs: Any?): Unit = global.console.log.apply(global.console, xs.toList().toJSArray())
inline fun cwarn(vararg xs: dynamic) = global.console.warn.apply(global.console, xs.toList().toJSArray())

inline fun dlog(vararg xs: dynamic) = clog("[DEBUG]", *xs)
inline fun dwarn(vararg xs: dynamic) = cwarn("[DEBUG]", *xs)

fun t(en: String, ru: String) = ru

@native val React: IReact = noImpl

@native interface IReact {
    fun createElement(tag: dynamic, attrs: dynamic, vararg children: dynamic): ReactElement
    fun createClass(def: dynamic): dynamic
}

fun el(tag: String, attrs: Json, vararg children: ReactElement?): ReactElement =
    React.createElement(tag, attrs, *children)

fun String?.asReactElement(): ReactElement? = this.asDynamicReactElement()

@native interface ReactElement

val NORE: ReactElement = null.asDynamic()
val NOTRE: ToReactElementable = NORE.toToReactElementable()

@native interface ReactClassInstance {
    fun forceUpdate()
}

fun qweqwe() {
    open class A
    class B : A()
    val p = Promise<A> {resolve, reject ->
        resolve(B())
    }
}

@native class Promise<T>(f: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    fun <U> then(onFulfilled: (T) -> Unit,
                 onRejected: ((Throwable) -> Unit)? = null): Promise<U> = noImpl

//    fun <U> then(cb: (T) -> Any?): Promise<U> = noImpl

    companion object {
        fun <T> resolve(value: T): Promise<T> = noImpl
    }
}


fun remoteProcedureNameForRequest(req: Any): String {
    val requestClassName = ctorName(req)
    return requestClassName.substring(0, requestClassName.length - "Request".length).decapitalize()
}


@native fun <T> __await(p: Promise<T>): T = noImpl
@native fun <T> __asyncResult(x: T): Promise<T> = noImpl
@native fun <T> __reawait(p: Promise<T>): Promise<T> = noImpl

@Front open class RequestMatumba {
    // TODO:vgrechka Why the fuck do I need `fields` in `hiddenFields` to be separate?
    val fields = mutableListOf<FormFieldFront<*>>()
    val hiddenFields = mutableListOf<HiddenFormFieldFront>()
}

abstract class HiddenFormFieldFront(val container: RequestMatumba, val name: String) {
    init {
        container.hiddenFields.add(this)
    }

    abstract fun populateRemote(json: Json)
}

abstract class FormFieldFront<Value>(val container: RequestMatumba, val name: String) {
    init {
        container.fields.add(this)
    }

    lateinit var form: FormMatumba<*, *>

    abstract fun render(): ReactElement
    abstract val value: Value

    abstract var error: String?
    abstract var disabled: Boolean
    abstract fun focus()
    abstract fun populateRemote(json: Json)
}

annotation class Front

@Front fun <E : Enum<E>> EnumHiddenField(
    container: RequestMatumba,
    name: String,
    values: Array<E>,
    possiblyUnspecified: Boolean = false
): HiddenField<E> {
    return HiddenField(container, name, possiblyUnspecified=possiblyUnspecified)
}

@Front fun StringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
): HiddenField<String> {
    return HiddenField(container, name, possiblyUnspecified=possiblyUnspecified)
}

@Front fun MaybeStringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
): HiddenField<String?> {
    return HiddenField(container, name, possiblyUnspecified=possiblyUnspecified)
}

@Front fun BooleanHiddenField(container: RequestMatumba, name: String): HiddenField<Boolean> {
    return HiddenField(container, name)
}

@Front class HiddenField<T>(container: RequestMatumba, name: String, val possiblyUnspecified: Boolean = false): HiddenFormFieldFront(container, name) {
    var value: T? = null
        set(value) {field = value; specified = true}

    private var specified = false

    // TODO:vgrechka Extract this generic toRemote()
    override fun populateRemote(json: Json) {
        if (!possiblyUnspecified && value == null) bitch("I want field $name specified")

        val dynaValue: dynamic = value
        json[name] = when {
            dynaValue == null -> null

            // TODO:vgrechka Reimplement once Kotlin-JS gets reflection    94315462-a862-4148-95a0-e45a0f73212d
            dynaValue.`name$` != null -> dynaValue.`name$` // Kinda enum

//            global.Array.isArray(dynaValue.array) -> {
//                jsArrayToList(dynaValue.array)
//            }

            else -> dynaValue
        }

        json["$name-specified"] = specified
    }
}

@Front class TextField(
    container: RequestMatumba,
    name: String,
    val title: String,
    type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
): FormFieldFront<String>(container, name) {

    override var error: String? = null

    val input = Shitus.Input(json(
        "tamy" to true,
        "type" to when (type) {
            TextFieldType.PASSWORD -> "password"
            else -> "text"
        },
        "kind" to when (type) {
            TextFieldType.TEXTAREA -> "textarea"
            else -> "input"
        },
        "volatileStyle" to {
            if (error != null) json("paddingRight" to 30)
            else undefined
        }
    ))

    override var value: String
        get() = input.getValue()
        set(value) { input.setValue(value) }

    override var disabled: Boolean
        get() = input.isDisabled()
        set(value) { input.setDisabled(value) }

    override fun focus() = input.focus()

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "TextField", "tamy" to name, "className" to "form-group"),
            if (title != null) Shitus.labela(json(), Shitus.spanc(json("tame" to "label", "content" to title))) else undefined,
            Shitus.diva(json("style" to json("position" to "relative")),
                input,
                if (error != null) errorLabel(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else undefined,
                if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else undefined))
    }

    override fun populateRemote(json: Json) {
        json[name] = value
    }
}

@Front class CheckboxField(container: RequestMatumba, name: String) : FormFieldFront<Boolean>(container, name) {
    override var error: String? = null

    val checkbox = Shitus.Checkbox(json("tamy" to true))

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "AgreeTermsField", "tame" to "AgreeTermsField", "className" to "form-group"),
            Shitus.diva(json("style" to json("display" to "flex")),
                checkbox,
                Shitus.diva(json("style" to json("width" to 5))),
                Shitus.diva(json(),
                    Shitus.spanc(json("tame" to "prose", "content" to t("I’ve read and agreed with ", "Я прочитал и принял "))),
                    Shitus.link(json("tamy" to true, "title" to t("terms and conditions", "соглашение"), "onClick" to {this.popupTerms()}))
                ),
                if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "borderRadius" to 10, "marginTop" to 3, "marginRight" to 9, "marginLeft" to "auto", "backgroundColor" to Color.RED_300))) else null
            ),
            if (error != null) errorLabel(json("name" to "agreeTerms", "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else null
        )
    }

    override var value: Boolean
        get() = checkbox.getValue()
        set(value) { checkbox.setValue(value) }

    override var disabled: Boolean
        get() = checkbox.isDisabled()
        set(value) { checkbox.setDisabled(value) }

    override fun focus() = dwarn("Need focus() for CheckboxField?")

    fun popupTerms() {
        global.alert("No fucking terms. You can go crazy with this shit...")
    }

    override fun populateRemote(json: Json) {
        json[name] = value
    }
}

//@native interface LegacyUIShit {
//    fun replaceNavigate(url: String): Promise<Unit>
//    fun pushNavigate(url: String): Promise<Unit>
//    fun setUser(newUser: UserRTO)
//    var token: String?
//    var signedUpOK: Boolean
//    var user: UserRTO
//    fun startLiveStatusPolling()
//    fun setPage(spec: Json)
//    fun urlLink(spec: Json): ReactElement
//    fun getUser(): UserRTO
//    fun signOut()
//    fun updatePage()
//    val urlQuery: Map<String, String>
//    var currentPage: Any?
//    fun loadPageForURL(): Promise<Unit>
//    fun setRootContent(re: ReactElement)
//    var updatePage: () -> Unit
//    var updatePageHeader: () -> Unit
//}

@Front class SelectField<T>(
    container: RequestMatumba,
    name: String,
    val title: String,
    val values: Array<T>
) : FormFieldFront<T>(container, name)
where T : Enum<T>, T : Titled {

    override var error: String? = null

    val select = Select(Attrs(tamy=""), values, null,
        onChange = {
            form.fieldChanged()
        },
        onFocus = {
            form.fieldFocused(this)
        },
        onBlur = {
            form.fieldBlurred(this)
        }

//        json(
//            "values" to values.map{json("value" to it.name, "title" to it.title)}.toJSArray(),
//            "tamy" to true,
//            "onChange" to {
//                form.fieldChanged()
//            },
//            "onFocus" to {
//                form.fieldFocused(this)
//            },
//            "onBlur" to {
//                form.fieldBlurred(this)
//            }
//        )
    )

    override fun render(): ReactElement {
        return Shitus.diva(json("controlTypeName" to "SelectField", "tamy" to name, "className" to "form-group"),
            // Can it be null?
            if (title != null) Shitus.labela(json(), Shitus.spanc(json("tame" to "label", "content" to title))) else null,
            Shitus.diva(json("style" to json("position" to "relative")),
                select.toReactElement(),
                if (error != null) errorLabel(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else null,
                if (error != null) Shitus.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else null))
    }

    override var value: T
        get() = select.value
        set(value) { select.setValue(value) }

    override var disabled: Boolean
        get() = select.isDisabled()
        set(value) { select.setDisabled(value) }

    override fun focus() = select.focus()

    override fun populateRemote(json: Json) {
        json[name] = select.value.name
    }

}

fun <Res> callMatumba(req: RequestMatumba, token: String?): Promise<Res> =
    callMatumba(remoteProcedureNameForRequest(req), req, token)

fun <Res> callMatumba(procedureName: String, req: RequestMatumba, token: String?): Promise<Res> {
    return callRemoteProcedurePassingJSONObject(procedureName, dyna{r->
        r.clientKind = global.CLIENT_KIND
        r.lang = global.LANG
        token?.let {r.token = it}

        r.fields = json()
        for (field in req.fields) field.populateRemote(r.fields)
        for (field in req.hiddenFields) field.populateRemote(r.fields)
    })
}

fun <Res> callZimbabwe(req: RequestMatumba, token: String?): Promise<ZimbabweResponse<Res>> =
    callZimbabwe(remoteProcedureNameForRequest(req), req, token)

fun <Res> callZimbabwe(procedureName: String, req: RequestMatumba, token: String?): Promise<ZimbabweResponse<Res>> {"__async"
    return __asyncResult(try {
        val res = __await<Any>(callRemoteProcedurePassingJSONObject(procedureName, dyna{r->
            r.clientKind = global.CLIENT_KIND
            r.lang = global.LANG
            token?.let {r.token = it}

            r.fields = json()
            for (field in req.fields) field.populateRemote(r.fields)
            for (field in req.hiddenFields) field.populateRemote(r.fields)
        }))
        when (res) {
            is FormResponse.Hunky<*> -> ZimbabweResponse.Hunky(cast(res.meat))
            is FormResponse.Shitty -> ZimbabweResponse.Shitty<Res>(res.error, listOf())
            else -> ZimbabweResponse.Hunky<Res>(cast(res))
        }
    } catch(e: Throwable) {
        spitExceptionToConsole(e)
        ZimbabweResponse.Shitty<Res>(t("TOTE", "Сервис временно в жопе, просим прощения"), listOf())
    })
}

fun spitExceptionToConsole(e: dynamic) {
    revealStack(e)
}

fun <Res> callDangerousMatumba(req: RequestMatumba): Promise<Res> {
    return callMatumba(req, js("typeof DANGEROUS_TOKEN === 'undefined' ? null : DANGEROUS_TOKEN")
        ?: bitch("This fucking client is built without DANGEROUS_TOKEN"))
}

fun printStack() {
    console.log(global.Error("Gimme the stack").stack)
}



















