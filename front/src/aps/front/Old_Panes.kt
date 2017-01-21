/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import jquery.JQuery
import jquery.jq
import org.w3c.dom.HTMLElement
import kotlin.browser.document

//class Pane(
//)

//private val panes = mutableListOf<Pane>()

//object Panes {
//    private val nodes = mutableListOf<HTMLElement>()
//
//    fun put(tre: ToReactElementable): Pane {
//        throw UnsupportedOperationException("Implement me, please, fuck you")
//    }
//
//    fun passivate() {
//        for (node in nodes) {
//            ReactDOM.unmountComponentAtNode(node)
//        }
//    }
//
//    fun activate() {
////        for (x in broEntries(Browseroid.current)) {
////            val node = byid0(x.htmlElementID)!!
////            ReactDOM.render(x.reactElement, node)
////            nodes += node
////        }
//    }
//}


val old_panes by lazy {Old_Panes("pane-", Browseroid.current.reactoid)}
val old_debugPanes by lazy {Old_Panes("debugPane-", Browseroid.current.reactoid)}

class Old_Panes(val idPrefix: String, val reactoidom: Reactoid) {
    val names = mutableSetOf<String>()
//    val nameToMountedShit = mutableMapOf<String, Reactoid.MountedShit>()

    fun put(name: String, parent: JQuery? = null, tre: ToReactElementable) {
//        remove(name)
        if (names.contains(name)) bitch("Pane already exists: $name")

        val id = nameToContainerID(name)
        val container = byid0(id) ?: run {
            (parent ?: jq(document.body!!)).append("<div id='$id'></div>")
            byid0ForSure(id)
        }

        reactoidom.mount(tre.toReactElement() ?: NORE, container)

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
        val id = nameToContainerID(name)
        val container = byid0(id) ?: bitch("No container: $id")
//        if (container != null) {
            _DOMReact.unmountComponentAtNode(container)
            container.remove()
//        }

        names.remove(name)
    }

    private fun nameToContainerID(name: String) = "$idPrefix$name"

    fun removeAll() = names.forEach {remove(it)}

    fun contains(name: String) = names.contains(name)

    fun hideAll() = setEachDisplay("none")
    fun showAll() = setEachDisplay("")

    private fun setEachDisplay(value: String) {
        for (name in names) {
            byid0(nameToContainerID(name))!!.style.display = value
        }
    }
}







