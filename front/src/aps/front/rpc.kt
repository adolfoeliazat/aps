/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

//fun igniteRPCShit() {
//    dlog("Igniting RPC shit...")
//}

fun fetchFromBackend(path: String, requestJSONObject: dynamic = null): Promise<dynamic> {"__async"
    val stackBeforeXHR: String = StackCaptureException().stack

    return Promise { resolve, reject ->
        val xhr = js("new XMLHttpRequest()")
        xhr.open("POST", "http://127.0.0.1:8080/$path")
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    val jsonObject = global.JSON.parse(xhr.responseText)
                    // dlog("Got backend response for /$path", global.JSON.stringify(jsonObject, null, 4))
                    resolve(jsonObject)
                } else {
                    reject(FatException("Got shitty backend response at /$path: status = ${xhr.status}", stackBeforeXHR))
                }
            }
        }

        xhr.send(global.JSON.stringify(requestJSONObject))
    }
}

fun dejsonize(jsThing: dynamic): Any? {
    return when {
        jsThing == null -> null

        jsTypeOf(jsThing).oneOf("string", "number", "boolean") -> jsThing

        jsThing.`$$$enum` != null -> eval("_.${jsThing.`$$$enum`}.${jsThing.value}")

        jsThing.`$$$class` != null ->
            evalAny("new _.${(jsThing.`$$$class` as String).replace("$", ".")}()").applet {res ->
                for (k in jsKeys(jsThing))
                    if (k != "\$\$\$class")
                        jsSet(res, k, dejsonize(jsThing[k]))
            }

        jsIsArray(jsThing) -> jsArrayToList(jsThing) {dejsonize(it)}

        else -> { dwarn("jsThing", jsThing); wtf("Dunno how to dejsonize that jsThing") }
    }
}

fun <Res> callRemoteProcedurePassingJSONObject(procedureName: String, requestJSONObject: dynamic): Promise<Res> {"__async"
    __dlog.requestJSONObject(procedureName, requestJSONObject)
    val responseJSONObject = __await(fetchFromBackend("rpc/$procedureName", requestJSONObject))
    __dlog.responseJSONObject(procedureName, global.JSON.stringify(responseJSONObject, null, 2))

    return __asyncResult(dejsonize(responseJSONObject) as Res)
}

@Deprecated("Old RPC")
fun <Res> callRemoteProcedure(procedureName: String, req: Request): Promise<Res> {"__async"
    val requestJSONObject = js("({})")
    val dynamicReq: dynamic = req
    for (k in jsArrayToList(global.Object.keys(req))) {
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






















