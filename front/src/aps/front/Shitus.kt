/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

@native var lodash: dynamic

object Shitus {
    val delay = ::jsFacing_delay
    val run = ::jsFacing_run
    val runa = ::jsFacing_runa
    val repeat = ::jsFacing_repeat
    val fov: dynamic = ::jsFacing_fov
    val fova = ::jsFacing_fova
    val tokens = ::jsFacing_tokens
    val values = ::jsFacing_values
    val invariant = ::jsFacing_invariant
    val raise = ::jsFacing_raise
    val isEqual = ::jsFacing_isEqual
    val clone = ::jsFacing_clone
    val isObject = ::jsFacing_isObject
    val noop: dynamic = {}
    val isBlank = ::jsFacing_isBlank
    val sortKeys = ::jsFacing_sortKeys
    val trim = lodash.trim
    val trimStart = lodash.trimStart
    val sortBy = lodash.sortBy
    val isEmpty = lodash.isEmpty
    val errorToMappedClientStackString: dynamic = ::jsFacing_errorToMappedClientStackString
    val makeT = ::jsFacing_makeT
    val dedent = ::jsFacing_dedent
    val omapo = ::jsFacing_omapo
    val uuid = ::jsFacing_uuid
    val codeLinesToString = ::jsFacing_codeLinesToString
    val fcomapo = ::jsFacing_fcomapo
    val invalidateKotlinStackSourceMapConsumer = ::jsFacing_invalidateKotlinStackSourceMapConsumer
    val captureStackAsException = ::jsFacing_captureStackAsException
    val omapa = ::jsFacing_omapa
}

fun jsFacing_arrayDeleteFirstThat(arr: dynamic, pred: dynamic): dynamic {
    for (i in 0 until arr.length) {
        if (pred(arr[i])) {
            arr.splice(i, 1)
            return arr
        }
    }
    return arr
}

fun jsFacing_deleteKey(obj: dynamic, key: dynamic) {
    js("delete obj[key]")
}

fun jsFacing_delay(ms: dynamic): dynamic {
    return newNativePromise({resolve: dynamic ->
        global.setTimeout(resolve, ms)
    })
}

fun jsFacing_run(f: dynamic): dynamic {
    return f()
}

fun jsFacing_runa(f: dynamic): Promise<dynamic> {"__async"
    return __asyncResult(__await(f()))
}

fun jsFacing_repeat(s: String, count: Int): String {
    return s.repeat(count)
}

fun jsFacing_fov(): dynamic {
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    return if (jsTypeOf(x) == "function") x.apply(null, all.slice(1)) else x
}

fun shittyFov(x: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x() else x
}

fun shittyFov(x: dynamic, y: dynamic): dynamic {
    return if (jsTypeOf(x) == "function") x(y) else x
}

fun jsFacing_fova(): Promise<dynamic> {"__async"
    val all = js("Array.prototype.slice.call(arguments)")
    val x = all[0]
    return if (jsTypeOf(x) == "function") __await<dynamic>(x.apply(null, x.slice(1))) else x
}

fun jsFacing_tokens(s: dynamic): dynamic {
    return s.trim().split(global.RegExp("\\s+"))
}

fun jsFacing_values(x: dynamic): dynamic {
    val res = js("[]")
    for (k in jsArrayToList(global.Object.keys(x)))
        res.push(x[k])
    return res
}

fun jsFacing_invariant(cond: dynamic, msg: dynamic) {
    if (!cond) {
        val message = "[INVARIANT VIOLATION] " + msg
        console.error(message)
        Shitus.raise(message)
    }
}

fun jsFacing_raise(msg: dynamic) {
    throw JSException(msg)
}

fun jsFacing_isEqual(a: Any?, b: Any?): Boolean {
    imf("jsFacing_isEqual")
}

fun jsFacing_clone(x: Any?): Any? {
    imf("jsFacing_clone")
}

fun jsFacing_isObject(value: dynamic): Boolean {
    val type = jsTypeOf(value)
    return value != null && (type == "object" || type == "function");
}

fun jsFacing_isBlank(s: dynamic): dynamic {
    return s == null || s.trim() == ""
}

fun jsFacing_sortKeys(o: dynamic) {
    if (global.Array.isArray(o)) {
        o.filter(::jsFacing_isObject).forEach(::jsFacing_sortKeys)
    } else if (jsFacing_isObject(o)) {
        var pairs = global.lodash.toPairs(o)
        pairs = global.lodash.sortBy(pairs, {x: dynamic -> x[0]})
        pairs.forEach({arg: dynamic ->
            val k = arg[0]
            jsFacing_deleteKey(o, k)
        })
        pairs.forEach({arg: dynamic ->
            val k = arg[0]
            val v = arg[1]
            o[k] = v
            if (jsFacing_isObject(v)) {
                jsFacing_sortKeys(v)
            }
        })
    }
}

fun jsFacing_errorToMappedClientStackString(shit: dynamic, _opts: dynamic = null): Promise<dynamic> {"__async"
    val opts = if (_opts != null) _opts else json("skipReactShit" to true, "skipMessage" to false)
    val skipReactShit  = opts.skipReactShit
    val skipMessage = opts.skipMessage

    val stack = if (jsTypeOf(shit) == "string") shit else shit.stack
    var lines = stack.split("\n")
    lines = __await(jsFacing_linesToMappedUsefulLines(lines))

    if (skipReactShit) {
        var reactShitMarkerSet: dynamic = null
        lines.forEach({s: dynamic, i: dynamic ->
            if (s.includes("React")) {
                if (!reactShitMarkerSet) {
                    lines[i] = "----- React shit is skipped -----"
                    reactShitMarkerSet = true
                } else {
                    lines[i] = undefined
                }
            }
        })
        lines = global.lodash.compact(lines)
    }

    return __asyncResult((if (skipMessage || jsTypeOf(shit) == "string") "" else shit.message + "\n") + lines.join("\n"))
}

fun jsFacing_linesToMappedUsefulLines(lines: dynamic): Promise<dynamic> {"__async"
    val clientStackSourceMapConsumer = __await<dynamic>(jsFacing_getClientStackSourceMapConsumer())
    val kotlinStackSourceMapConsumer = __await<dynamic>(jsFacing_getKotlinStackSourceMapConsumer())
//    const scalaStackSourceMapConsumer = await getScalaStackSourceMapConsumer()

    val usefulLines = jsArrayOf()
    lines.forEach({_lineText: dynamic, i: dynamic ->
        var lineText = _lineText
        var smapConsumer: dynamic = null
        var sliceLineFrom: dynamic = null

        val koti = lineText.indexOf("front-enhanced.js:")
        if (koti != -1) {
            // dlog("koti line: [" + lineText + "]")
            smapConsumer = kotlinStackSourceMapConsumer
            sliceLineFrom = koti + "front-enhanced.js:".length
        }

        val bjsi = lineText.indexOf("bundle.js:")
        if (bjsi != -1) {
            // dlog("bjsi line: [" + lineText + "]")
            smapConsumer = clientStackSourceMapConsumer
            sliceLineFrom = bjsi + "bundle.js:".length
        }

        if (!smapConsumer) return@forEach Unit

        val someShit = lineText.slice(sliceLineFrom, lineText.length - 1).split(":")
        val line = someShit[0]
        val column = someShit[1]
//        const [line, column] = lineText.slice(sliceLineFrom, lineText.length - 1).split(":")
        val pos = smapConsumer.originalPositionFor(json("line" to parseInt(line), "column" to parseInt(column)))

        // Members of returned thing can be null
        // https://github.com/mozilla/source-map/blob/182f4459415de309667845af2b05716fcf9c59ad/lib/source-map-consumer.js#L637
        if (pos.source != null) {
            var lineTextWithoutPos: dynamic = null
            if (lineText.startsWith("    at http://") || lineText.startsWith("    at https://")) {
                lineTextWithoutPos = "    at god knows where"
            } else {
                lineTextWithoutPos = lineText.slice(0, lineText.indexOf(" ("/*)*/))
            }

            lineText = lineTextWithoutPos + " (${pos.source}:${pos.line}:${pos.column})"
            lineText = lineText.replace("file://E:/work/aps", "APS")
        }

        usefulLines.push(lineText)
    })

    return __asyncResult(usefulLines)
}

var clientStackSourceMapConsumerPromise: dynamic = null

fun jsFacing_getClientStackSourceMapConsumer(): Promise<Any> {"__async"
    if (clientStackSourceMapConsumerPromise == null) {
        clientStackSourceMapConsumerPromise = jsFacing_createClientStackSourceMapConsumer()
    }
    val clientStackSourceMapConsumer = __await<dynamic>(clientStackSourceMapConsumerPromise)
    if (!clientStackSourceMapConsumer) throw JSException("We are fucked, man. No clientStackSourceMapConsumer")

    return __asyncResult(clientStackSourceMapConsumer)
}

fun jsFacing_createClientStackSourceMapConsumer(): Promise<Any> {"__async"
    val logTime = jsFacing_beginLogTime("createClientStackSourceMapConsumer")
    try {
        try {
            val response = __await<dynamic>(global.superagent.get("bundle.js"))
            val text = response.text
            val smcIndex = text.lastIndexOf("//# sourceMappingURL=data:application/json;")
            if (smcIndex != -1) {
                val arg = global.convertSourceMap.fromComment(text.slice(smcIndex)).toJSON()
                return js("new global.sourceMap.SourceMapConsumer(arg)")
            } else {
                return dlog("No inline source map found in bundle.js")
            }
        } catch (e: Throwable) {
            return dlog("Failed to make bundle source map consumer: ${e}")
        }
    } finally {
        logTime.end()
    }
}


var kotlinStackSourceMapConsumerPromise: dynamic = null

fun jsFacing_getKotlinStackSourceMapConsumer(): Promise<Any> {"__async"
    if (!kotlinStackSourceMapConsumerPromise) {
        kotlinStackSourceMapConsumerPromise = jsFacing_createKotlinStackSourceMapConsumer()
    }
    val kotlinStackSourceMapConsumer = __await<dynamic>(kotlinStackSourceMapConsumerPromise)
    if (!kotlinStackSourceMapConsumer) throw JSException("We are fucked, man. No kotlinStackSourceMapConsumer")

    return kotlinStackSourceMapConsumer
}

fun jsFacing_createKotlinStackSourceMapConsumer(): Promise<Any> {"__async"
    val logTime = jsFacing_beginLogTime("createKotlinStackSourceMapConsumer")
    try {
        try {
            val response = __await<dynamic>(global.superagent.get("kotlin/front.js.map"))
            val text = response.text
            val arg = global.JSON.parse(text)
            return js("new sourceMap.SourceMapConsumer(arg)")
        } catch (e: Throwable) {
            return dlog("Failed to make Kotlin source map consumer: ${e}")
        }
    } finally {
        logTime.end()
    }
}

fun jsFacing_beginLogTime(what: dynamic): dynamic {
    val t0 = global.Date.now()
    dlog("--- BEGIN ${what}")

    return json(
        "end" to {
            val elapsed = global.Date.now() - t0
            dlog("--- END ${what} in ${elapsed}ms")
        }
    )
}

fun jsFacing_makeT(lang: dynamic): dynamic {
    fun t() {
        val all = js("Array.prototype.slice.call(arguments)")
        if (!all[0]) throw JSException("I don’t want falsy first argument in t()")
        var ss: dynamic = null
        if (jsTypeOf(all[0]) == "object") {
            ss = all[0]
            ss.EN = ss.EN || ss.en
            ss.UA = ss.UA || ss.ua
        } else {
            ss = json("EN" to all[0], "UA" to (all[1] || all[0]))
        }

        val res = ss[lang.toUpperCase()]
        if (!res) throw JSException("Localize me: " + global.nodeUtil.inspect(json("lang" to lang, "arguments" to all), json("depth" to null)))
        return res
    }

    return ::t
}

fun jsFacing_dedent(it: dynamic): dynamic {
    val lines = it.split("\\r?\\n")
    if (lines.length && !lines[0].trim()) {
        lines.shift()
    }
    if (lines.length && !lines[lines.length - 1].trim()) {
        lines.pop()
    }

    var minIndent = global.Number.MAX_SAFE_INTEGER
    for (line in jsArrayToList(lines)) {
        if (line.trim()) {
            val lineIndent = line.length - line.trimLeft().length
            if (lineIndent < minIndent) {
                minIndent = lineIndent
            }
        }
    }

    return lines.map({line: dynamic ->
        if (!line.trim()) "" else line.slice(minIndent)
    }).join('\n')
}

fun jsFacing_omapo(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).reduce({res: dynamic, k: dynamic, i: dynamic ->
        res[k] = f(o[k], k, i)
        res
    }, json())
}

fun jsFacing_uuid(): String {
    imf("jsFacing_uuid (do it on server, cause node-uuid is +1.5M)")
}

fun jsFacing_codeLinesToString(arg: dynamic): dynamic {
    val codeLines: dynamic = arg.codeLines
    val indent: dynamic = arg.indent

    var res = ""
    for (line in jsArrayToList(codeLines)) {
        res += " ".repeat(indent) + (line || "".asDynamic()) + "\n"
    }
    return res
}

fun jsFacing_fcomapo(o: dynamic, f: dynamic): dynamic {
    return jsFacing_omapo(jsFacing_focompact(o), f)
}

fun jsFacing_focompact(o: dynamic) {
    return jsFacing_ocompact(jsFacing_ofov(o))
}

fun jsFacing_ofov(o: dynamic) {
    return jsFacing_omapo(o, {v: dynamic -> Shitus.fov(v)})
}

fun jsFacing_ocompact(o: dynamic) {
    return jsFacing_ofilter(o, {v: dynamic -> !jsFacing_nilf(v)})
}

fun jsFacing_ofilter(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).reduce({res: dynamic, k: dynamic, i: dynamic ->
        if(f(o[k], k, i)) res[k] = o[k]
        res
    }, json())
}

fun jsFacing_nilf(x: dynamic): Boolean {
    return x == null || x == false
}

fun jsFacing_invalidateKotlinStackSourceMapConsumer() {
    kotlinStackSourceMapConsumerPromise = null
}

fun jsFacing_captureStackAsException(msg: String?): dynamic {
    try {
        throw JSException(msg ?: "Gimme fucking stack")
    } catch (e: Throwable) {
        return e
    }
}

fun jsFacing_omapa(o: dynamic, f: dynamic): dynamic {
    return global.Object.keys(o).map {k: dynamic, i: dynamic -> f(o[k], k, i)}
}






