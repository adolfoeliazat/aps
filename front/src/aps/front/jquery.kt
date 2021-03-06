/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import org.w3c.dom.*
import kotlin.browser.*

external class JQuery() {
    fun addClass(className: String): JQuery
//    fun addClass(f: Element.(Int, String) -> String): JQuery

    fun attr(attrName: String): String
    fun attr(attrName: String, value: String): JQuery

    fun html(): String
    fun html(s: String): JQuery
//    fun html(f: Element.(Int, String) -> String): JQuery


    fun hasClass(className: String): Boolean
    fun removeClass(className: String): JQuery
    fun height(): Number
    fun width(): Number

    fun click(): JQuery

//    fun mousedown(handler: Element.(MouseEvent) -> Unit): JQuery
//    fun mouseup(handler: Element.(MouseEvent) -> Unit): JQuery
//    fun mousemove(handler: Element.(MouseEvent) -> Unit): JQuery

//    fun dblclick(handler: Element.(MouseClickEvent) -> Unit): JQuery
//    fun click(handler: Element.(MouseClickEvent) -> Unit): JQuery

//    fun load(handler: Element.() -> Unit): JQuery
//    fun change(handler: Element.() -> Unit): JQuery

    fun append(str: String): JQuery
    fun ready(handler: () -> Unit): JQuery
    fun text(text: String): JQuery
    fun slideUp(): JQuery
//    fun hover(handlerInOut: Element.() -> Unit): JQuery
//    fun hover(handlerIn: Element.() -> Unit, handlerOut: Element.() -> Unit): JQuery
    fun next(): JQuery
    fun parent(): JQuery
    fun `val`(): String?
}

//open external class MouseEvent() {
//    val pageX: Double
//    val pageY: Double
//    fun preventDefault()
//    fun isDefaultPrevented(): Boolean
//}

//external class MouseClickEvent() : MouseEvent {
//    val which: Int
//}

@JsName("$")
external fun jq(selector: String): JQuery
@JsName("$")
external fun jq(selector: String, context: Element): JQuery
@JsName("$")
external fun jq(callback: () -> Unit): JQuery
@JsName("$")
external fun jq(obj: JQuery): JQuery
@JsName("$")
external fun jq(el: Element): JQuery
@JsName("$")
external fun jq(): JQuery



external interface JQueryPosition {
    val left: Double
    val top: Double
}

operator fun JQuery.get(index: Int): HTMLElement? = this.asDynamic()[index]
fun JQuery.scrollTop(value: Double): Unit = this.asDynamic().scrollTop(value)
fun JQuery.scrollTop(value: Int): Unit = this.asDynamic().scrollTop(value)
fun JQuery.scrollTop(): Double = this.asDynamic().scrollTop()
fun JQuery.offset(): JQueryPosition = this.asDynamic().offset()
fun JQuery.text(): String = this.asDynamic().text()
fun JQuery.remove(): String = this.asDynamic().remove()
val JQuery.length: Int get() = this.asDynamic().length
fun JQuery.css(prop: String, value: Any?): JQuery = this.asDynamic().css(prop, value)
fun JQuery.setVal(value: String?): JQuery = this.asDynamic().`val`(value)
fun JQuery.outerWidth(includeMargin: Boolean = false): Double = this.asDynamic().outerWidth(includeMargin)
fun JQuery.outerHeight(includeMargin: Boolean = false): Double = this.asDynamic().outerHeight(includeMargin)
fun JQuery.each(block: (index: Int, element: HTMLElement) -> Unit): Unit = this.asDynamic().each(block)
fun JQuery.hide(): String = this.asDynamic().hide()
fun JQuery.show(): String = this.asDynamic().show()

val jqbody: JQuery get() = jq(document.body!!)
val jqwindow: JQuery get() = js("$(window)")

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

fun HTMLElement.hasScrollbar() = scrollHeight > clientHeight
























