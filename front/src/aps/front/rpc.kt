/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*

fun fetchURL(url: String, method: String, data: String?): Promise<String> {"__async"
    val stackBeforeXHR: String = CaptureStackException().stack

    return Promise {resolve, reject ->
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

val backendURL = "http://127.0.0.1:8080"

fun fetchFromBackend(path: String, requestJSONObject: dynamic = null): Promise<dynamic> = async {
    val obj = await(fetchFromURL("POST", "$backendURL/$path", global.JSON.stringify(requestJSONObject)) {
        global.JSON.parse(it)
    })

    val fields: CommonResponseFields = obj
    Globus.world?.let {
        if (!path.contains("getSoftwareVersion"))
            it.footer.setBackendVersion(fields.backendVersion)
    }

    obj
}

fun <T> fetchFromURL(method: String, url: String, data: Any?, transform: (String) -> T): Promise<T> {
    val stackBeforeXHR: String = CaptureStackException().stack

    return Promise {resolve, reject ->
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

fun <T> dejsonize(json: String): T? = dejsonizeValue(JSON.parse(json))

fun <T> dejsonizeValue(jsThing: dynamic): T? {
    try {
        val noise = DebugNoise("dejsonize", mute = true)

        noise.clog("jsThing", jsThing)

        return when {
            jsThing == null -> null

            jsTypeOf(jsThing).oneOf("string", "number", "boolean") -> jsThing

            jsThing.`$$$enum` != null -> {
                val enumName = jsThing.`$$$enum` as String
                val code = "_.${enumName.replace("\$", ".")}.${jsThing.value}"
                noise.clog("code", code)
                eval(code)
            }

            jsThing.`$$$class` != null -> {
                val clazz: String = jsThing.`$$$class`
                when (clazz) {
                    "kotlin.Unit" -> Unit.asDynamic()
                    else -> {
                        val inst = eval("new _.${clazz.replace("$", ".")}()")
                        for (k in jsKeys(jsThing))
                            if (k != "\$\$\$class")
                                jsSet(inst, k, dejsonizeValue(jsThing[k]))
                        inst
                    }
                }
            }

            jsThing.`$$$primitiveish` != null -> {
                val typeName: String = jsThing.`$$$primitiveish`
                val stringValue: String? = jsThing.value
                val code =
                    if (stringValue == null) "null"
                    else when (typeName) {
                        "long" -> "new Kotlin.Long(stringValue)"
                        else -> wtf("Primitiveish typeName: $typeName")
                    }
                noise.clog("code", code)
                eval(code)
            }

            jsIsArray(jsThing) -> jsArrayToListOfDynamic(jsThing) {dejsonizeValue(it)} .asDynamic()

            else -> wtf("Dunno how to dejsonize that jsThing")
        }
    } catch(e: Throwable) {
        console.error(e.message)
        console.error("Offending jsThing", jsThing)
        throw e
    }
}

fun <Res> callRemoteProcedurePassingJSONObject(procedureName: String, requestJSONObject: CommonRequestFields): Promise<Res> {"__async"
//    __dlog.requestJSONObject(procedureName, requestJSONObject)
    requestJSONObject.rootRedisLogMessageID = Globus.rootRedisLogMessageID
    requestJSONObject.databaseID = ExternalGlobus.DB
    val responseJSONObject = __await(fetchFromBackend("rpc/$procedureName", requestJSONObject))
//    __dlog.responseJSONObject(procedureName, global.JSON.stringify(responseJSONObject, null, 2))

    return __asyncResult(dejsonizeValue<Res>(responseJSONObject)!!)
}

@Deprecated("Old RPC")
fun <Res> callRemoteProcedure(procedureName: String, req: Request): Promise<Res> {"__async"
    val requestJSONObject = js("({})")
    val dynamicReq: dynamic = req
    for (k in jsArrayToListOfDynamic(global.Object.keys(req))) {
        val dynamicValue = dynamicReq[k]
        requestJSONObject[k] = when {
            dynamicValue == null -> null

            // TODO:vgrechka Reimplement once Kotlin-JS gets reflection    94315462-a862-4148-95a0-e45a0f73212d
            dynamicValue.`name$` != null -> dynamicValue.`name$` // Kinda enum

//            global.Array.isArray(dynamicValue.array) -> {
//                jsArrayToList(dynamicValue.array)
//            }

            else -> dynamicValue
        }
    }

    return __await(callRemoteProcedurePassingJSONObject(procedureName, requestJSONObject))
}



//fun <Res> callRemoteProcedure(procedureName: String, req: RequestMatumba, token: String?): Promise<Res> {"__async"
//    return __await(callRemoteProcedurePassingJSONObject(procedureName, dyna {r ->
//        r.clientKind = global.CLIENT_KIND
//        r.lang = global.LANG
//        token?.let {r.token = it}
//
//        r.fields = js("({})")
//
////        r.arg = dyna {arg ->
////            val dynamicReq: dynamic = req
////            for (k in jsArrayToList(global.Object.keys(req))) {
////                val dynamicValue = dynamicReq[k]
////                arg[k] = when {
////                    dynamicValue == null -> null
////
////                    // TODO:vgrechka Reimplement once Kotlin-JS gets reflection    94315462-a862-4148-95a0-e45a0f73212d
////                    dynamicValue.`name$` != null -> dynamicValue.`name$` // Kinda enum
////
//////                    global.Array.isArray(dynamicValue.array) -> {
//////                        jsArrayToList(dynamicValue.array)
//////                    }
////
////                    else -> dynamicValue
////                }
////            }
////        }
//    }))
//}






















