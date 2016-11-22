/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import jquery.JQuery
import jquery.jq
import kotlin.browser.document

object DebugPanes {
    val names = mutableSetOf<String>()

    fun put(name: String, parent: JQuery = jq(document.body!!), tre: ToReactElementable) {
        remove(name)

        val id = "debugPanes-${name}"
        val container = byid0(id) ?: run {
            parent.append("<div id='${id}'></div>")
            byid0ForSure(id)
        }

        DOMReact.render(tre.toReactElement(), container)

        names.add(name)
    }

    fun put(name: String, tre: ToReactElementable) {
        put(name, jq(document.body!!), tre)
    }

    fun remove(name: String) {
        val id = "debugPanes-${name}"
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

