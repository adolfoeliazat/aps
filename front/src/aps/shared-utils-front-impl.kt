/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps

import aps.front.*
import into.kommon.*
import kotlin.js.Json
import kotlin.js.json
import kotlin.properties.Delegates.notNull

//fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)
//fun imf(what: String = "me"): Nothing = throw JSException("Implement $what, please, fuck you")
//fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw JSException("WTF: $msg")
//fun die(msg: String = "You killed me, motherfucker..."): Nothing = throw JSException(msg)

inline fun clog(vararg xs: Any?): Unit = global.console.log.apply(global.console, xs.toList().toJSArray())
inline fun cwarn(vararg xs: dynamic): Unit = global.console.warn.apply(global.console, xs.toList().toJSArray())

inline fun dlog(vararg xs: dynamic) = clog("[DEBUG]", *xs)
inline fun dwarn(vararg xs: dynamic) = cwarn("[DEBUG]", *xs)

fun t(en: String, ru: String) = ru

external val React: IReact

external interface IReact {
    fun createElement(tag: dynamic, attrs: dynamic, vararg children: dynamic): ReactElement
    fun createClass(def: dynamic): dynamic
}

fun el(tag: String, attrs: Json, vararg children: ReactElement?): ReactElement =
    React.createElement(tag, attrs, *children)

fun String?.asReactElement(): ReactElement? = this.asDynamicReactElement()

external class ReactElement

val NORE: ReactElement = null.asDynamic()
val NOTRE: ToReactElementable = NORE.toToReactElementable()

external interface ReactClassInstance {
    fun forceUpdate()
}

//class Promisoid<out T>(val promise: Promise<ThenableShitHidingWrapper<T>>) {
//    fun then(onFulfilled: (T) -> Unit,
//             onRejected: ((Throwable) -> Unit)? = null) {
//        promise.then<Nothing>(
//            onFulfilled = {onFulfilled(it.thenableShit)},
//            onRejected = {onRejected?.invoke(it)}
//        )
//    }
//
//    companion object {
//        fun <T> resolve(value: T): Promisoid<T> {
//            return Promisoid(Promise.resolve(ThenableShitHidingWrapper(value)))
//        }
//    }
//}

class Promisoid<out T>(f: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    private val nativePromise = Promise<ThenableShitHidingWrapper<T>> {resolveNative, rejectNative ->
        fun resolve(value: T) {
            resolveNative(ThenableShitHidingWrapper(value))
        }

        fun reject(exception: Throwable) {
            rejectNative(exception)
        }

        f(::resolve, ::reject)
    }

    fun then(onFulfilled: (T) -> Unit) {
        nativePromise.then<Nothing>(
            onFulfilled = {onFulfilled(it.thenableShit)})
    }

    fun then(onFulfilled: (T) -> Unit, onRejected: ((Throwable) -> Unit)? = null) {
        nativePromise.then<Nothing>(
            onFulfilled = {onFulfilled(it.thenableShit)},
            onRejected = onRejected)
    }

    companion object {
        fun <T> resolve(value: T): Promisoid<T> {
            imf()
//            return Promisoid(Promise.resolve(ThenableShitHidingWrapper(value)))
        }
    }
}

external class Promise<out T>(f: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
    fun <U> then(onFulfilled: (T) -> Unit,
                 onRejected: ((Throwable) -> Unit)? = definedExternally): Promise<U>

    companion object {
        fun <T> resolve(value: T): Promise<T>
    }
}

fun remoteProcedureNameForRequest(req: Any): String {
    val requestClassName = ctorName(req)
    return requestClassName.substring(0, requestClassName.length - "Request".length)
//    return requestClassName.substring(0, requestClassName.length - "Request".length).decapitalize()
}

@Front open class RequestMatumba {
    // TODO:vgrechka Why the fuck do I need `fields` and `hiddenFields` to be separate?
    val fields = mutableListOf<FormFieldFront>()
    val hiddenFields = mutableListOf<HiddenFormFieldFront>()
    var fieldInstanceKeySuffix: String? = null
}

abstract class HiddenFormFieldFront(val container: RequestMatumba, val name: String) {
    init {
        container.hiddenFields.add(this)
    }

    abstract fun populateRemote(json: Json): Promisoid<Unit>
}

//abstract class FormFieldFront<Value>(val container: RequestMatumba, val name: String) {
abstract class FormFieldFront(val container: RequestMatumba, val name: String) {
    init {
        container.fields.add(this)
    }

    lateinit var form: FormMatumba<*, *>

    abstract fun render(): ReactElement
//    abstract val value: Value

    abstract var error: String?
    abstract var disabled: Boolean
    abstract fun focus()
    abstract fun populateRemote(json: Json): Promisoid<Unit>
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

@Front fun stringHiddenField() = eagerEx<RequestMatumba, HiddenField<String>> {thisRef, property ->
    StringHiddenField(thisRef, property.name)
}

@Front fun StringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
): HiddenField<String> {
    return HiddenField(container, name, possiblyUnspecified=possiblyUnspecified)
}

@Front fun longHiddenField() = eagerEx<RequestMatumba, LongHiddenField> {thisRef, property ->
    LongHiddenField(thisRef, property.name)
}

@Front class LongHiddenField(container: RequestMatumba, name: String): HiddenFormFieldFront(container, name) {
    var value by notNull<Long>()

    override fun populateRemote(json: Json) = async {
        json[name] = value.toString()
    }
}

@Front fun maybeLongHiddenField() = eagerEx<RequestMatumba, MaybeLongHiddenField> {thisRef, property ->
    MaybeLongHiddenField(thisRef, property.name)
}

@Front class MaybeLongHiddenField(container: RequestMatumba, name: String): HiddenFormFieldFront(container, name) {
    var value: Long? = null

    override fun populateRemote(json: Json) = async {
        json[name] = value?.toString()
    }
}

@Front fun MaybeStringHiddenField(
    container: RequestMatumba,
    name: String,
    possiblyUnspecified: Boolean = false
): HiddenField<String?> {
    return HiddenField(container, name, possiblyUnspecified=true)
}

@Front fun maybeStringHiddenField() = eagerEx<RequestMatumba, HiddenField<String?>> {thisRef, property ->
    MaybeStringHiddenField(thisRef, property.name)
}

@Front fun BooleanHiddenField(container: RequestMatumba, name: String): HiddenField<Boolean> {
    return HiddenField(container, name)
}

@Front class HiddenField<T>(container: RequestMatumba, name: String, val possiblyUnspecified: Boolean = false): HiddenFormFieldFront(container, name) {
    var value: T? = null
        set(value) {field = value; specified = true}

    private var specified = false

    // TODO:vgrechka Extract this generic toRemote()
    override fun populateRemote(json: Json): Promisoid<Unit> = async {
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



external interface IKillMeInput {
    fun getValue(): String
    fun setValue(value: String)
    fun isDisabled(): Boolean
    fun setDisabled(value: Boolean)
    fun focus()
}





fun <Res> callMatumba(req: RequestMatumba, token: String?, wideClientKind: WideClientKind? = null, descr: String? = null): Promisoid<Res> =
    callMatumba(remoteProcedureNameForRequest(req), req, token, wideClientKind = wideClientKind, descr = descr)

fun <Res> callMatumba(procedureName: String, req: RequestMatumba, token: String?, wideClientKind: WideClientKind? = null, descr: String? = null, populateFields: (Json) -> Unit = {}): Promisoid<Res> = async {
    val wck = wideClientKind ?: WideClientKind.User(Globus.clientKind)
    val payload = js("({})")
    putWideClientKind(payload, wck)
    payload.lang = global.LANG
    token?.let {payload.token = it}

    payload.fields = json()
    for (field in req.fields) await(field.populateRemote(payload.fields))
    for (field in req.hiddenFields) await(field.populateRemote(payload.fields))
    populateFields(payload.fields)

    await(callRemoteProcedurePassingJSONObject<Res>(procedureName, payload, wck, descr = descr))
}

private fun putWideClientKind(payload: dynamic, wck: WideClientKind) {
    payload.wideClientKind = wck::class.simpleName
    if (wck is WideClientKind.User) {
        payload.clientKind = wck.kind.name
    }
}

fun <Res> callZimbabwe(req: RequestMatumba, token: String?): Promisoid<ZimbabweResponse<Res>> =
    callZimbabwe(remoteProcedureNameForRequest(req), req, token)

fun <Res> callZimbabwe(procedureName: String, req: RequestMatumba, token: String?): Promisoid<ZimbabweResponse<Res>> = async {
    try {
        val wck = WideClientKind.User(Globus.clientKind)
        val payload = js("({})")
        putWideClientKind(payload, wck)
        payload.lang = global.LANG
        token?.let {payload.token = it}

        payload.fields = json()
        for (field in req.fields) await(field.populateRemote(payload.fields))
        for (field in req.hiddenFields) await(field.populateRemote(payload.fields))

        TestGlobal.requestPause?.let {await(it.promise)}
        val res = await<Any>(callRemoteProcedurePassingJSONObject(procedureName, payload, wck))

        when (res) {
            is FormResponse.Hunky<*> -> ZimbabweResponse.Hunky(cast(res.meat))
            is FormResponse.Shitty -> ZimbabweResponse.Shitty<Res>(res.error, listOf())
            else -> ZimbabweResponse.Hunky<Res>(cast(res))
        }
    } catch(e: Throwable) {
        spitExceptionToConsole(e)
        ZimbabweResponse.Shitty<Res>(const.msg.serviceFuckedUp, listOf())
    }
}

fun spitExceptionToConsole(e: dynamic) {
    revealStack(e)
}

fun <Res> callDangerousMatumba(req: RequestMatumba): Promisoid<Res> {
    return callMatumba(
        req = req,
        token = js("typeof DANGEROUS_TOKEN === 'undefined' ? null : DANGEROUS_TOKEN") ?: bitch("This fucking client is built without DANGEROUS_TOKEN"),
        wideClientKind = WideClientKind.Test())
}

suspend fun <Res> callDangerousMatumba2(req: RequestMatumba): Res {
    return await(callMatumba(
        req = req,
        token = js("typeof DANGEROUS_TOKEN === 'undefined' ? null : DANGEROUS_TOKEN") ?: bitch("This fucking client is built without DANGEROUS_TOKEN"),
        wideClientKind = WideClientKind.Test(),
        descr = req::class.simpleName))
}

fun printStack() {
    console.log(global.Error("Gimme the stack").stack)
}

val CaptureStackException.prettyCapturedStack: Promisoid<String> get() = async {
    val lines = stack.lines().toMutableList()
    lines.removeAt(0)
    while (lines[0].contains(CaptureStackException::class.simpleName!!)) {
        lines.removeAt(0)
    }

    await(stackToMappedClientStackString(lines.joinToString("\n")))
}



















