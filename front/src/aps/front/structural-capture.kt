package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import kotlin.browser.document
import kotlin.browser.window



fun captureShit(): Json = json()-{o->
    o["url"] = loc.href
    o["documentTitle"] = document.title
    o["windowScrollY"] = window.scrollY
    o["body"] = captureNode(document.body!!)!!
}

fun captureNode(node: Node): Json? {
    return when (node.nodeType) {
        Node.COMMENT_NODE -> null

        Node.ELEMENT_NODE -> {
            node as Element
            if (node.tagName == "SCRIPT") return null
            json()-{o->
                o["nodeType"] = node.nodeType
                o["tagName"] = node.tagName
                o["attributes"] = json()-{o->
                    for (i in 0..node.attributes.length - 1) {
                        o[node.attributes[i]!!.name] = node.attributes[i]!!.value
                    }
                }
                o["computedStyle"] = json()-{o->
                    window.getComputedStyle(node).let {style->
                        for (i in 0..style.length - 1) {
                            val prop = style[i]!!
                            o[prop] = json()-{o->
                                o["value"] = style.getPropertyValue(prop)
                                o["priority"] = style.getPropertyPriority(prop)
                            }
                        }
                    }
                }
                o["childNodes"] = (mutableListOf<Json>()-{a->
                    for (i in 0..node.childNodes.length - 1) {
                        captureNode(node.childNodes[i]!!)?.let {
                            a += it
                        }
                    }
                }).toJSArray()
            }
        }

        Node.TEXT_NODE -> json()-{o->
            node as Text
            o["nodeType"] = node.nodeType
            o["wholeText"] = node.wholeText
        }

        else -> wtf("nodeType: ${node.nodeType}")
    }
}

data class ElementDims(
    val left: Double,
    val top: Double,
    val width: Double,
    val height: Double
)

data class DiffNode(
    val capture: Json,
    val absoluteDims: ElementDims
)

class DiffStructuralCapture(val oldCapture: Json, val newCapture: Json) {
    val removedNodes = mutableListOf<DiffNode>()

    init {
        descend()
        dump()
    }

    fun descend() {

    }

    fun dump() {
        clog("removedNodes:")
        removedNodes.forEach {clog("*", it)}
    }
}

fun spikeCaptureStructuralShit() {
    val capture1 = captureShit()

    val header = jq("#148")
    val button = jq("#159")
    val navItem = jq("#234")
    val emailInput = jq("#110")
    val passwordInput = jq("#114")
    val passwordLabel = jq("#171")
    header.text("pizda").css("color", "pink")
    button.text("Fuck it").css("width", "300px").css("background", "rosybrown")
    navItem.text("Shit").css("font-weight", "bold")
    emailInput.setVal("I am fucker, really")
    passwordLabel.remove()
    passwordInput.remove()
    button.parent().append("<button style='font-style: italic; margin-left: 1rem;'>Nope</button>")

    val capture2 = captureShit()

    val diff = DiffStructuralCapture(capture1, capture2)

//    console.log(JSON.stringify(capture))
}
















//    o["attributes"] = json()-{o->
//        for (i in 0..el.attributes.length - 1) {
//            o[el.attributes[i]!!.name] = el.attributes[i]!!.value
//        }
//    }


//o["attributes"] = (mutableListOf<Json>()-{a->
//    for (i in 0..node.attributes.length - 1) {
//        a += json()-{o->
//            o["name"] = node.attributes[i]!!.name
//            o["value"] = node.attributes[i]!!.value
//        }
//    }
//}).toJSArray()

