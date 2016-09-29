/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

import aps.front.global
import aps.front.toJSArray


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




