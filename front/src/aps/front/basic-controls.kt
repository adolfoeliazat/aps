/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun toReactElementable(x: Any?): ToReactElementable {
    return object : ToReactElementable {
        override fun toReactElement(): ReactElement {
            return asReactElement(x)
        }
    }
}

interface DefinitionStackHolder {
    fun promiseDefinitionStack(): Promisoid<dynamic>
}

//fun renderExpandableOnDemandStack(definitionStackHolder: DefinitionStackHolder): ToReactElementable {
//    return object : Control(ControlInstanceSpec()) {
//        var state = "virgin"
//
//        override fun defaultControlTypeName() = "renderExpandableOnDemandStack"
//
//        override fun render(): ReactElement {
//            return diva {
//                - when (state) {
//                    "virgin" -> toReactElementable(Shitus.link(json("content" to "Show stack", "onClick" to {
//                        state = "loading"
//                        runni {
//                            async {
//                                val jsarray = await<dynamic>(definitionStackHolder.promiseDefinitionStack())
//                                console.log("--- got definition stack", jsarray)
//                            }
//                        }
//                        update()
//                    })))
//
//                    "loading" -> spana {style {fontStyle = "italic"}; - "Loading..."}
//
//                    else -> wtf(state)
//                }
//
//            }.toReactElement()
//        }
//    }
//}

fun label(title: String) = klabel {it-title}
fun span(s: String? = null, style: Style = Style()) = kspan(style = style) {it-s}
fun span(s: String? = null, className: String?) = kspan(className = className) {it-s}
fun div(s: String? = null) = kdiv {it-s}
fun div(s: String? = null, className: String?, style: Style = Style()) = kdiv(Attrs(className = className), style) {it-s}





















