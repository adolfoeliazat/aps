/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import kotlin.browser.window
import kotlin.js.json

fun fetchURL(url: String, method: String, data: String?): Promisoid<String> {
    val stackBeforeXHR: String = CaptureStackException().stack

    return Promisoid {resolve, reject ->
        val xhr = js("new XMLHttpRequest()")
        xhr.open(method, url)
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    val response: String = xhr.responseText
                    resolve(response)
                } else {
                    reject(FatException("Got shitty response from $url: status = ${xhr.status}", stackBeforeXHR))
                }
            }
        }

        xhr.send(data)
    }
}

fun fetchFromBackend(path: String, requestJSONObject: dynamic = null): Promisoid<dynamic> = async {
    val obj = await(fetchFromURL_killme("POST", "${fconst.backendURL}/$path", global.JSON.stringify(requestJSONObject)) {
        global.JSON.parse(it)
    })

    val fields: CommonResponseFields = obj
    Globus.worldMaybe?.let {
        if (it.booted && !path.contains("getSoftwareVersion"))
            it.footer.setBackendVersion(fields.backendVersion)
    }

    obj
}

fun <T> fetchFromURL_killme(method: String, url: String, data: Any?, transform: (String) -> T): Promisoid<T> {
    val stackBeforeXHR: String = CaptureStackException().stack

    return Promisoid {resolve, reject ->
        val xhr = js("new XMLHttpRequest()")
        xhr.open(method, url)
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    val response: String = xhr.responseText
                    val result = transform(response)
                    // dlog("Got backend response for /$path", global.JSON.stringify(result, null, 4))
                    resolve(result)
                } else {
                    reject(FatException("Got shitty backend response from $url: status = ${xhr.status}", stackBeforeXHR))
                }
            }
        }

        xhr.send(data)
    }
}

suspend fun <T> fetchFromURL(method: String, url: String, data: Any?, transform: (String) -> T): T {
    val stackBeforeXHR: String = CaptureStackException().stack
    return await(Promisoid {resolve, reject ->
        val xhr = js("new XMLHttpRequest()")
        xhr.open(method, url)
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    val response: String = xhr.responseText
                    val result = transform(response)
                    // dlog("Got backend response for /$path", global.JSON.stringify(result, null, 4))
                    resolve(result)
                } else {
                    reject(FatException("Got shitty backend response from $url: status = ${xhr.status}", stackBeforeXHR))
                }
            }
        }

        xhr.send(data)
    })
}

fun <T> dejsonize(json: String, descr: String? = null): T {
    gloshit.dejsonize_json = json
    return dejsonizeValue(JSON.parse(json), descr)
}

var dejsonizeValue_lastInvocationID = -1
fun <T> dejsonizeValue(jsThing: dynamic, descr: String? = null): T {
    // XXX 5d324703-9255-4a67-ba25-ecc865194418
    val invocationID = ++dejsonizeValue_lastInvocationID
    var inst: dynamic = null
    try {
        gloshit.pizda = TestSQLFiddleRequest.Response("qqq", true)
        val logShit = false
        if (logShit) {
            val shit = JSON.stringify(jsThing).substring(0, 5000)
            if (!shit.contains("GetSoftwareVersionRequest") && descr != null) {
                gloshit.dejsonizeValue_jsThing = jsThing
                clog("dejsonizeValue", descr, shit)
            }
        }

        val res = when {
            jsThing == null -> null

            jsTypeOf(jsThing).oneOf("string", "number", "boolean") -> jsThing

            jsThing.`$$$enum` != null -> {
                val enumName = jsThing.`$$$enum` as String
                val code = "_.${enumName.replace("\$", ".")}.${jsThing.value}"
                eval(code)
            }

            jsThing.`$$$class` != null -> {
                val clazz: String = jsThing.`$$$class`
                when (clazz) {
                    "kotlin.Unit" -> Unit.asDynamic()
                    else -> {
                        inst = eval("new _.${clazz.replace("$", ".")}()")
                        for (k in jsKeys(jsThing)) {
                            if (k != "\$\$\$class") {
                                val jsValue = jsThing[k]
                                val kotlinValue = dejsonizeValue<Any?>(jsValue)
                                setPropertyOrBackingField(inst, k, kotlinValue)
                                if (jsTypeOf(jsValue) == "boolean") // XXX
                                    jsSet(inst, "is" + k.capitalize(), kotlinValue)
                            }
                        }
                        inst
                    }
                }
            }

            jsThing.`$$$primitiveish` != null -> {
                val typeName: String = jsThing.`$$$primitiveish`
                val stringValue: String? = jsThing.value

                when {
                    stringValue == null -> null
                    else -> when (typeName) {
                        "long" -> stringValue.toLong()
                        else -> wtf("Primitiveish typeName: $typeName")
                    }
                }

//                val code =
//                    if (stringValue == null) "null"
//                    else when (typeName) {
////                        "long" -> "toLong(stringValue)"
////                        "long" -> "new Kotlin.Long(stringValue)"
//                        else -> wtf("Primitiveish typeName: $typeName")
//                    }
//                eval(code)
            }

            jsThing.`$$$mapOfStringToAnyQuestion` == true -> {

            }

            jsIsArray(jsThing) -> jsArrayToListOfDynamic(jsThing) {dejsonizeValue(it)}.asDynamic()

            else -> wtf("Dunno how to dejsonize that jsThing")
        }

        return res as T
    }
    catch(e: Throwable) {
        console.error(e.message)
        console.error("Offending jsThing (invocationID=$invocationID)", jsThing)
        gloshit["jsThing$invocationID"] = jsThing
        gloshit["inst$invocationID"] = inst
        throw e
    }
}

fun jsonize(shit: Any?): String {
    val out = jsonizeToObject(shit)
    // clog("json", js("JSON").stringify(out, null, 2))
    val json = js("JSON").stringify(out)
    return json
}

fun jsonizeToObject(shit: Any?): Any? {
    gloshit.jsonizeToObject_shit = shit
    if (shit == null) return null
    val jsType = jsTypeOf(shit)
    if (jsType.oneOf("string", "number", "boolean")) return shit
    if (jsType != "object") wtf("jsType: $jsType")

    when (shit) {
        is List<*> -> {
            return Array(shit.size) {i->
                jsonizeToObject(shit[i])
            }
        }

        else -> {
            val out = json()
            out["\$\$\$class"] = "aps." + shit!!::class.simpleName

            val protoProps = JSObject.getOwnPropertyNames(shit.asDynamic().__proto__).toSet() - setOf("constructor")
            for (protoProp in protoProps) {
                val value = shit.asDynamic()[protoProp]
                out[protoProp] = jsonizeToObject(value)
            }

            return out
        }
    }
}

fun jsonize2(shit: Any?): String {
    val out = jsonizeToObject2(shit)
    val json = js("JSON").stringify(out)
    return json
}

fun jsonizeToObject2(shit: Any?): Any? {
    fun noise(vararg xs: Any?) {/*dlog("jsonizeToObject2", *xs)*/}

    gloshit.jsonizeToObject_shit = shit
    if (shit == null) return null
    val jsType = jsTypeOf(shit)
    if (jsType.oneOf("string", "number", "boolean")) return shit
    if (jsType != "object") wtf("jsType: $jsType")

    when (shit) {
        is List<*> -> {
            return Array(shit.size) {i->
                jsonizeToObject2(shit[i])
            }
        }
        is Long -> {
            return shit.toString()
        }
        else -> {
            val out = json()
            val className = shit::class.simpleName
            noise("className", className)
            out["\$\$\$class"] = "aps." + className

            val props = JSObject.getOwnPropertyNames(shit.asDynamic())
            val protoProps = JSObject.getOwnPropertyNames(shit.asDynamic().__proto__).toSet() - setOf("constructor")
            for (prop in protoProps + props) {
                val value = shit.asDynamic()[prop]
                noise("prop", prop, value)
                out[prop] = jsonizeToObject2(value)
            }

            return out
        }
    }
}

fun <Res> callRemoteProcedurePassingJSONObject(procedureName: String, requestJSONObject: CommonRequestFields, wideClientKind: WideClientKind, descr: String? = null): Promisoid<Res> = async {
    requestJSONObject.rootRedisLogMessageID = Globus.rootRedisLogMessageID
    requestJSONObject.databaseID = ExternalGlobus.DB
    if (wideClientKind is WideClientKind.User) {
        requestJSONObject.clientURL = windowLocationHrefToClientURL(loc.href)
    }
    requestJSONObject.fakeEmail = Globus.isTest
    val responseJSONObject = await(fetchFromBackend("rpc/$procedureName", requestJSONObject))

    dejsonizeValue<Res>(responseJSONObject, descr = descr)!!
}

fun windowLocationHrefToClientURL(href: String): String {
    if (href.contains(".local")) {
        val slashIndex = href.indexOf("/", "http://".size)
        if (slashIndex == -1) return href
        else return href.substring(0, slashIndex)
    }

    val ghPrefix = "https://staticshit.github.io/"
    if (href.startsWith(ghPrefix)) {
        val slashIndex = href.indexOf("/", ghPrefix.size)
        if (slashIndex == -1) return href
        else return href.substring(0, slashIndex)
    }

    bitch("Fucky URL: $href")
}

@Deprecated("Old RPC")
fun <Res> callRemoteProcedure(procedureName: String, req: Request): Promisoid<Res> = async {
    die("kill me: callRemoteProcedure")
//    val requestJSONObject = js("({})")
//    val dynamicReq: dynamic = req
//    for (k in jsArrayToListOfDynamic(global.Object.keys(req))) {
//        val dynamicValue = dynamicReq[k]
//        requestJSONObject[k] = when {
//            dynamicValue == null -> null
//
//            // TODO:vgrechka Reimplement once Kotlin-JS gets reflection    94315462-a862-4148-95a0-e45a0f73212d
//            dynamicValue.`name$` != null -> dynamicValue.`name$` // Kinda enum
//
////            global.Array.isArray(dynamicValue.array) -> {
////                jsArrayToList(dynamicValue.array)
////            }
//
//            else -> dynamicValue
//        }
//    }
//
//    return __await(callRemoteProcedurePassingJSONObject(procedureName, requestJSONObject))
}

























