package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.HTMLElement
import kotlin.js.json

interface Reactoid {
    interface MountedShit {
        fun unmount()
    }

    fun mount(rel: ReactElement, container: HTMLElement)

//    fun render(rel: ReactElement?, container: HTMLElement)
//    fun unmountComponentAtNode(container: HTMLElement)
}

external object ReactDOMServer {
    fun renderToStaticMarkup(el: ReactElement): String
}

object _DOMReact {
    data class Root(val hel: HTMLElement, val stackCapture: CaptureStackException)

    private val _roots = mutableListOf<Root>()

    fun render(rel: ReactElement?, hel: HTMLElement) {
        ReactDOM.render(rel, hel)

        val newRoot = Root(hel, CaptureStackException())
        val i = _roots.indexOfFirst {it.hel == hel}
        if (i == -1)
            _roots += newRoot
        else
            _roots[i] = newRoot
    }

    fun unmountComponentAtNode(hel: HTMLElement) {
        ReactDOM.unmountComponentAtNode(hel)

        val i = _roots.indexOfFirst {it.hel == hel}
        if (i == -1) {
            gloshit.unmountComponentAtNode = json("_roots" to _roots, "hel" to hel)
            bitch("Obscure hel: #${hel.id}")
        }
        _roots.removeAt(i)
    }

    fun checkNothingMounted() {
        val roots = _roots.toList()
        if (roots.isNotEmpty()) {
            async {
                console.error(buildString {
                    appendln("${roots.size} leftover React root(s):")
                    for ((i, root) in roots.withIndex()) {
                        lnappendln2("${i + 1}) Shit")
                        appendln(await(root.stackCapture.prettyCapturedStack))
                    }
                })
            }
            throw IllegalStateException("checkNothingMounted")
        }
    }

}

external object ReactDOM {
    fun render(rel: ReactElement?, container: HTMLElement): Unit
    fun unmountComponentAtNode(container: HTMLElement): Unit
}


























