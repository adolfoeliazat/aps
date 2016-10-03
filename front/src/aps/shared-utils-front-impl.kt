/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.front.*

fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)
fun imf(what: String = "me"): Nothing = throw JSException("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw JSException("WTF: $msg")
fun die(msg: String = "You killed me, motherfucker..."): Nothing = throw JSException(msg)

fun clog(vararg xs: dynamic) = global.console.log.apply(global.console, xs.toList().toJSArray())
fun cwarn(vararg xs: dynamic) = global.console.warn.apply(global.console, xs.toList().toJSArray())

fun dlog(vararg xs: dynamic) = clog("[DEBUG]", *xs)
fun dwarn(vararg xs: dynamic) = cwarn("[DEBUG]", *xs)

fun t(en: String, ru: String) = ru

@native val React: IReact = noImpl

@native interface IReact {
    fun createElement(tag: dynamic, attrs: dynamic, vararg children: dynamic): ReactElement
    fun createClass(def: dynamic): dynamic
}

@native interface ReactElement {
}

@native class Promise<T>(f: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    fun <U> then(cb: (T) -> Any?): Promise<U> = noImpl
}

fun remoteProcedureNameForRequest(req: Any): String {
    val dynamicDummyReq: dynamic = req
    val requestClassName: String = dynamicDummyReq.__proto__.constructor.`$$$kindaPackageKey`
    return requestClassName.substring(0, requestClassName.length - "Request".length).decapitalize()
}

@Deprecated("Old RPC")
fun <Res> callRemoteProcedure(req: Request): Promise<Res> {
    val dynamicReq: dynamic = req
    val requestClassName: String = dynamicReq.__proto__.constructor.`$$$kindaPackageKey`
    val procedureName = requestClassName.substring(0, requestClassName.length - "Request".length).decapitalize()
    return callRemoteProcedure(remoteProcedureNameForRequest(req), req)
}

fun <Res> callRemoteProcedure(req: RequestMatumba, ui: LegacyUIShit): Promise<Res> {
    return callRemoteProcedure(remoteProcedureNameForRequest(req), req, ui)
}

@native fun <T> __await(p: Promise<T>): T = noImpl
@native fun <T> __asyncResult(x: T): Promise<T> = noImpl

//object __await {
//    inline infix operator fun <T> div(p: Promise<T>): T = __await(p)
//}

//object awaita {
//    inline infix operator fun <T> div(p: Promise<T>): Promise<T> {"__async"; "qweqweqwe"; return __await(p) as Promise<T>}
//}


open class RequestMatumba { // Front
    val fields = mutableListOf<FormFieldFront>()
}

abstract class FormFieldFront(val container: RequestMatumba, val name: String) {
    init {
        container.fields.add(this)
    }

    abstract fun render(): ReactElement
    abstract val value: String

    abstract var error: String?
    abstract var disabled: Boolean
}

annotation class Front

@Front class TextField(
    container: RequestMatumba,
    name: String,
    val title: String,
    type: TextFieldType,
    val minLen: Int,
    val maxLen: Int,
    val minDigits: Int = -1
): FormFieldFront(container, name) {

    override var error: String? = null

    val input = jshit.Input(json(
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

    override fun render(): ReactElement {
        return jshit.diva(json("controlTypeName" to "TextField", "tamy" to name, "className" to "form-group"),
            if (title != null) jshit.labela(json(), jshit.spanc(json("tame" to "label", "content" to title))) else undefined,
            jshit.diva(json("style" to json("position" to "relative")),
                input,
                if (error != null) jshit.errorLabel(json("name" to name, "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else undefined,
                if (error != null) jshit.diva(json("style" to json("width" to 15, "height" to 15, "backgroundColor" to Color.RED_300, "borderRadius" to 10, "position" to "absolute", "right" to 8, "top" to 10))) else undefined))
    }
}

@Front class CheckboxField(container: RequestMatumba, name: String) : FormFieldFront(container, name) {
    override var error: String? = null

    val checkbox = jshit.Checkbox(json("tamy" to true))

    override fun render(): ReactElement {
        return jshit.diva(json("controlTypeName" to "AgreeTermsField", "tame" to "AgreeTermsField", "className" to "form-group"),
            jshit.diva(json("style" to json("display" to "flex")),
                checkbox,
                jshit.diva(json("style" to json("width" to 5))),
                jshit.diva(json(),
                    jshit.spanc(json("tame" to "prose", "content" to t("I’ve read and agreed with ", "Я прочитал и принял "))),
                    jshit.link(json("tamy" to true, "title" to t("terms and conditions", "соглашение"), "onClick" to this::popupTerms))
                ),
                if (error != null) jshit.diva(json("style" to json("width" to 15, "height" to 15, "borderRadius" to 10, "marginTop" to 3, "marginRight" to 9, "marginLeft" to "auto", "backgroundColor" to Color.RED_300))) else null
            ),
            if (error != null) jshit.errorLabel(json("name" to "agreeTerms", "title" to error, "style" to json("marginTop" to 5, "marginRight" to 9, "textAlign" to "right"))) else null
        )
    }

    override var value: String
        get() = checkbox.getValue()
        set(value) { checkbox.setValue(value) }

    override var disabled: Boolean
        get() = checkbox.isDisabled()
        set(value) { checkbox.setDisabled(value) }

    fun popupTerms() {
        global.alert("No fucking terms. You can go crazy with this shit...")
    }
}

@native interface LegacyUIShit {
    fun replaceNavigate(url: String): Promise<Unit>
    fun pushNavigate(url: String): Promise<Unit>
    fun setUser(newUser: UserRTO)
    var token: String?
    var signedUpOK: Boolean
    var user: UserRTO
    fun startLiveStatusPolling()
    fun setPage(spec: Json)
    fun urlLink(spec: Json): dynamic
    fun getUser(): UserRTO
    fun signOut()
}


















