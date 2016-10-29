/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

fun toReactElementable(x: Any?): ToReactElementable {
    return object : ToReactElementable {
        override fun toReactElement(): ReactElement {
            return asReactElement(x)
        }
    }
}

interface DefinitionStackHolder {
    fun promiseDefinitionStack(): Promise<dynamic>
}

fun renderExpandableOnDemandStack(definitionStackHolder: DefinitionStackHolder): ToReactElementable {
    return object : Control(ControlInstanceSpec()) {
        var state = "virgin"

        override fun defaultControlTypeName() = "renderExpandableOnDemandStack"

        override fun render(): ReactElement {
            return diva {
                - when (state) {
                    "virgin" -> toReactElementable(Shitus.link(json("content" to "Show stack", "onClick" to {
                        state = "loading"
                        runni {"__async"
                            val jsarray = __await<dynamic>(definitionStackHolder.promiseDefinitionStack())
                            console.log("--- got definition stack", jsarray)
                        }
                        update()
                    })))

                    "loading" -> spana {style {fontStyle = "italic"}; - "Loading..."}

                    else -> wtf(state)
                }

            }.toReactElement()
        }
    }
}












