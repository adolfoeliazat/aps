/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import jquery.JQuery
import jquery.jq
import kotlin.browser.document

val panes = Panes("pane-")
val debugPanes = Panes("debugPane-")

class Panes(val idPrefix: String) {
    val names = mutableSetOf<String>()

    fun put(name: String, parent: JQuery? = null, tre: ToReactElementable) {
        remove(name)

        val id = "$idPrefix$name"
        val container = byid0(id) ?: run {
            (parent ?: jq(document.body!!)).append("<div id='$id'></div>")
            byid0ForSure(id)
        }

        DOMReact.render(tre.toReactElement(), container)

        names.add(name)
    }

    fun put(name: String, tre: ToReactElementable) {
        put(name, jq(document.body!!), tre)
    }

    fun put(parent: JQuery, tre: ToReactElementable): String {
        val name = puid()
        put(name, parent, tre)
        return name

    }
    fun put(tre: ToReactElementable): String {
        val name = puid()
        put(name, tre)
        return name
    }

    fun remove(name: String) {
        val id = "$idPrefix$name"
        val container = byid0(id)
        if (container != null) {
            DOMReact.unmountComponentAtNode(container)
            container.remove()
        }

        names.remove(name)
    }

    fun removeAll() = names.forEach { remove(it)}

    fun contains(name: String) = names.contains(name)
}


