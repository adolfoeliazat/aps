/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

class JSException(override val message: String) : Throwable(message) {
    val stack = js("Error")(message).stack
}

fun imf(what: String = "me"): Nothing = throw JSException("Implement $what, please, fuck you")
fun wtf(msg: String = "...WTF didn't you describe this WTF?"): Nothing = throw JSException("WTF: $msg")
fun die(msg: String = "You killed me, motherfucker..."): Nothing = throw JSException(msg)
fun bitch(msg: String = "Just bitching..."): Nothing = throw JSException(msg)

fun asReactElement(x: Any?): ReactElement {
    val didi: dynamic = x
    return didi
}

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
                    "virgin" -> toReactElementable(jshit.link(json("content" to "Show stack", "onClick" to {
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












