/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun igniteRPCShit() {
    dlog("Igniting RPC shit...")
//    shouldLoadMeta = true
}

//var shouldLoadMeta = true
//val classNameToFieldDeserializationInfos = mutableMapOf<String, Iterable<FieldDeserializationInfo>>()

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
//    if (shouldLoadMeta) {
//        // {classes: [{name: '', fields: [{name: '', strategy: {type: 'Enum', ...}}]}]}
//        fun loadStrategy(jsonObject: dynamic): TypeDeserializationStrategy = when (jsonObject.type) {
//            "Simple" -> TypeDeserializationStrategy.Simple()
//            "Enum" -> TypeDeserializationStrategy.Enum(jsonObject.enumClassName)
//            "Class" -> TypeDeserializationStrategy.Class(jsonObject.className)
//            "List" -> TypeDeserializationStrategy.List(loadStrategy(jsonObject.itemStrategy))
//            else -> wtf("Strategy type: ${jsonObject.type}")
//        }
//
//        val metaJSONObject = __await(fetchFromBackend("meta"))
////        dlog("Got meta", js("JSON").stringify(metaJSONObject, null, 2))
//        classNameToFieldDeserializationInfos.clear()
//        for (classJSONObject in jsArrayToList(metaJSONObject.classes)) {
//            val fdis = mutableListOf<FieldDeserializationInfo>()
//            for (fieldJSONObject in jsArrayToList(classJSONObject.fields)) {
//                fdis.add(FieldDeserializationInfo(
//                    fieldName = fieldJSONObject.name,
//                    strategy = loadStrategy(fieldJSONObject.strategy)))
//            }
//            classNameToFieldDeserializationInfos[classJSONObject.name] = fdis
//        }
//
//        shouldLoadMeta = false
//    }

    // dlog("requestJSONObject", requestJSONObject)

    val responseJSONObject = __await(fetchFromBackend("rpc/$procedureName", requestJSONObject))
    dlog("Response", js("JSON").stringify(responseJSONObject, null, 2))

    return __asyncResult(dejsonize(responseJSONObject) as Res)
}

fun <Res> callRemoteProcedure(procedureName: String, req: dynamic): Promise<Res> {"__async"
//    val requestJSONObject = js("({})")
//    for ((k, v) in req) {
//        requestJSONObject[k] = v
//    }

    return __await(callRemoteProcedurePassingJSONObject(procedureName, req))
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

//        jsIsArray(jsThing) -> mutableListOf<Any?>().applet {res ->
//        }

        else -> { dwarn("jsThing", jsThing); wtf("Dunno how to dejsonize that jsThing") }
    }
}

//fun <T> bak_dejsonize(jsonObject: dynamic, className: String): T {
//    val res = eval("new _.$className()")
//    val fds = classNameToFieldDeserializationInfos[className] ?: wtf("No field deserialization infos for $className")
//    for (fd in fds) {
//        res[fd.fieldName] = jsonValueToFuckingValue(jsonObject[fd.fieldName], fd.strategy)
//    }
//    return res
//}

//fun jsonValueToFuckingValue(jsonValue: dynamic, strategy: TypeDeserializationStrategy): Any? =
//    if (jsonValue == null) null
//    else when (strategy) {
//        is TypeDeserializationStrategy.Simple -> jsonValue
//        is TypeDeserializationStrategy.Enum -> eval("_.${strategy.enumClassName}.$jsonValue")
//        is TypeDeserializationStrategy.Class -> dejsonize(jsonValue, strategy.className)
//        is TypeDeserializationStrategy.List -> jsArrayToList(jsonValue, { jsonValueToFuckingValue(it, strategy.itemStrategy) })
//    }


fun rpc(req: ResetTestDatabaseRequest): Promise<ResetTestDatabaseRequest> = callRemoteProcedure("resetTestDatabase", req)
fun rpc(req: ImposeNextRequestTimestampRequest): Promise<ImposeNextRequestTimestampRequest> = callRemoteProcedure("imposeNextRequestTimestamp", req)


