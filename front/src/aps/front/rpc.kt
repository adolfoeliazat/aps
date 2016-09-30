/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun igniteRPCShit() {
    dlog("Igniting RPC shit...")
}

fun fetchFromBackend(path: String, requestJSONObject: dynamic = null): Promise<dynamic> {"__async"
    val stackBeforeXHR: String = js("Error().stack")

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
                    reject(JSException("Got shitty backend response at /$path: status = ${xhr.status}", stackBeforeXHR))
                }
            }
        }

        xhr.send(global.JSON.stringify(requestJSONObject))
    }
}

fun <Res> callRemoteProcedurePassingJSONObject(procedureName: String, requestJSONObject: dynamic): Promise<Res> {"__async"
    // dlog("requestJSONObject", requestJSONObject)
    val responseJSONObject = __await(fetchFromBackend("rpc/$procedureName", requestJSONObject))
    dlog("responseJSONObject ", global.JSON.stringify(responseJSONObject, null, 2))

    return __asyncResult(dejsonize(responseJSONObject) as Res)
}

fun <Req, Res> callRemoteProcedure(procedureName: String, req: Req): Promise<Res> {"__async"
    val requestJSONObject = js("({})")
    val dynamicReq: dynamic = req
    for (k in jsArrayToList(global.Object.keys(req))) {
        requestJSONObject[k] = dynamicReq[k]
    }

    return __await(callRemoteProcedurePassingJSONObject(procedureName, requestJSONObject))
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

        jsIsArray(jsThing) -> jsArrayToList(jsThing)

        else -> { dwarn("jsThing", jsThing); wtf("Dunno how to dejsonize that jsThing") }
    }
}

fun <T : Request> rpc(req: T): Promise<T> {
    global.shit = req
    val dynamicReq: dynamic = req
    val requestClassName: String = dynamicReq.__proto__.constructor.`$$$kindaPackageKey`
    val procedureName = requestClassName.substring(0, requestClassName.length - "Request".length).decapitalize()
    return callRemoteProcedure(procedureName, req)
}


