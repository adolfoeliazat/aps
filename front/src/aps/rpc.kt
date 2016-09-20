/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.front.JSException

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

fun rpc(req: HiRequest): Promise<HiResponse> = callRemoteProcedure("hi", req)

fun <Req, Res> callRemoteProcedure(procedureName: String, req: Req): Promise<Res> {
    return Promise<Res> {resolve, reject ->
        val modelName = "Hi"

        val xhr = js("new XMLHttpRequest()")
        xhr.open("POST", "http://127.0.0.1:8080/rpc/$procedureName", true)
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded")

        xhr.onreadystatechange = {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    val jsonObject = global.JSON.parse(xhr.responseText)
                    val res = eval("new _.aps.${modelName}Response()")
                    for (k in jsArrayToIterable(global.Object.keys(res))) {
                        res[k] = jsonObject[k]
                    }
                    resolve(res)
                } else {
                    reject(JSException("Got a shitty RPC response: status = ${xhr.status}"))
                }
            }
        }

        val dynamicReq: dynamic = req
        val jsonObject = js("({})")
        for (k in jsArrayToIterable(global.Object.keys(req))) {
            jsonObject[k] = dynamicReq[k]
        }
        xhr.send(global.JSON.stringify(jsonObject))
    }
}


