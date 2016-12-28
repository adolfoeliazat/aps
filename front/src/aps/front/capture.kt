package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import kotlin.browser.document
import kotlin.browser.window

fun captureShit(): Json = json()-{o->
    o["url"] = window.location.href
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


fun spikeCaptureShit() {
    val capture = captureShit()
    console.log(JSON.stringify(capture))
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

