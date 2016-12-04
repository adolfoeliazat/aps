/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import org.w3c.dom.*
import jquery.*
import kotlin.browser.*

@native interface JQueryPosition {
    val left: Int
    val top: Int
}

operator fun JQuery.get(index: Int): HTMLElement? = this.asDynamic()[index]
fun JQuery.scrollTop(value: Int): Unit = this.asDynamic().scrollTop(value)
fun JQuery.scrollTop(): Int = this.asDynamic().scrollTop()
fun JQuery.offset(): JQueryPosition = this.asDynamic().offset()
fun JQuery.text(): String = this.asDynamic().text()
fun JQuery.remove(): String = this.asDynamic().remove()

val jqbody: JQuery get() = jq(document.body!!)

fun byid(id: String): JQuery {
    val selector = "#$id".replace(Regex("\\."), "\\.")
    return jq(selector)
}

fun byid0(id: String): HTMLElement? {
    val selector = "#$id".replace(Regex("\\."), "\\.")
    return jq(selector)[0]
}

fun byid0ForSure(id: String): HTMLElement {
    return requireNotNull(byid0(id)) {"I want fucking element #$id"}
}

fun JQuery.not(selector: String): JQuery = this.asDynamic().not(selector)


























