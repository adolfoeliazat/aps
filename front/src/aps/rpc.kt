/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.front.JSException
import aps.front.wtf

fun igniteRPCShit() {
    println("Igniting RPC shit...")
    global.testRPC1 = ::testRPC1
}

fun testRPC1() {"__async"
    run {
        console.log("Sending request for Billy...")
        val res: HiResponse = __await(rpc(HiRequest(name = "Billy")))
        console.log("Got response", res.toString())
    }

    run {
        console.log("Sending request for Wilma...")
        val res: HiResponse = __await(rpc(HiRequest(name = "Wilma")))
        console.log("Got response", res.toString())
    }
}

class FieldMeta {
    enum class Type {
        STRING
    }
}

var shouldLoadMeta = true
val classNameToFieldDeserializationInfos = mutableMapOf<String, Iterable<FieldDeserializationInfo>>()

fun fetchFromBackend(path: String, requestJSONObject: dynamic = null): Promise<dynamic> {"__async"
    return Promise {resolve, reject ->
        val xhr = js("new XMLHttpRequest()")
        xhr.open("POST", "http://127.0.0.1:8080/$path")
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    console.log("Got backend response for /$path", xhr.responseText)
                    val jsonObject = global.JSON.parse(xhr.responseText)
                    resolve(jsonObject)
                } else {
                    reject(JSException("Got a shitty backend response: status = ${xhr.status}"))
                }
            }
        }

        xhr.send(global.JSON.stringify(requestJSONObject))
    }
}

fun <Req, Res> callRemoteProcedure(procedureName: String, req: Req): Promise<Res> {"__async"
    if (shouldLoadMeta) {
        // {classes: [{name: '', fields: [{name: '', strategy: {type: 'Enum', ...}}]}]}

        fun loadStrategy(jsonObject: dynamic): TypeDeserializationStrategy = when (jsonObject.type) {
            "Simple" -> TypeDeserializationStrategy.Simple()
            "Enum" -> TypeDeserializationStrategy.Enum(jsonObject.enumClassName)
            "Class" -> TypeDeserializationStrategy.Class(jsonObject.className)
            "List" -> TypeDeserializationStrategy.List(loadStrategy(jsonObject.itemStrategy))
            else -> wtf("Strategy type: ${jsonObject.type}")
        }

        val metaJSONObject = __await(fetchFromBackend("meta"))
        classNameToFieldDeserializationInfos.clear()
        for (classJSONObject in jsArrayToList(metaJSONObject.classes)) {
            val fdis = mutableListOf<FieldDeserializationInfo>()
            for (fieldJSONObject in jsArrayToList(classJSONObject.fields)) {
                fdis.add(FieldDeserializationInfo(
                    fieldName = fieldJSONObject.name,
                    strategy = loadStrategy(fieldJSONObject.strategy)))
            }
            classNameToFieldDeserializationInfos[classJSONObject.name] = fdis
        }
        shouldLoadMeta = false
    }

    val requestJSONObject = js("({})")
    val dynamicReq: dynamic = req
    for (k in jsArrayToList(global.Object.keys(req))) {
        requestJSONObject[k] = dynamicReq[k]
    }

    val responseJSONObject = __await(fetchFromBackend("rpc/$procedureName"))
    return __asyncResult(jsonObjectToFuckingObject(responseJSONObject , "aps.${procedureName.capitalize()}Response"))
}

fun <T> jsonObjectToFuckingObject(jsonObject: dynamic, className: String): T {
    val res = eval("new _.$className()")
    val fds = classNameToFieldDeserializationInfos[className] ?: wtf("No field deserialization infos for $className")
    for (fd in fds) {
        res[fd.fieldName] = jsonValueToFuckingValue(jsonObject[fd.fieldName], fd.strategy)
    }
    return res
}

fun jsonValueToFuckingValue(jsonValue: dynamic, strategy: TypeDeserializationStrategy): Any? =
    if (jsonValue == null) null
    else when (strategy) {
        is TypeDeserializationStrategy.Simple -> jsonValue
        is TypeDeserializationStrategy.Enum -> eval("_.aps.${strategy.enumClassName}.$jsonValue")
        is TypeDeserializationStrategy.Class -> jsonObjectToFuckingObject(jsonValue, strategy.className)
        is TypeDeserializationStrategy.List -> jsArrayToList(jsonValue, {jsonValueToFuckingValue(it, strategy.itemStrategy)})
    }


fun rpc(req: HiRequest): Promise<HiResponse> = callRemoteProcedure("hi", req)
fun rpc(req: SignInWithPasswordRequest): Promise<SignInWithPasswordResponse> = callRemoteProcedure("signInWithPassword", req)


