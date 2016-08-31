/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import Color.*
import FontWeight.*

fun testJSObject() {
    val jso = js("({})")
    jso.foo = 10
    jso.bar = 20
}

enum class Color(val css: String) {
    RED("red"),
    GREEN("green"),
    BLUE("blue"),
    ROSYBROWN("rosybrown")
}

enum class FontWeight(val css: String) {
    BOLD("bold")
}

fun makeTestShit() = object {
    val shit1 = React.createElement("div", json(
            "style" to json("border" to "2px solid red"),
            "onClick" to { println("cliiiiiick") },
            "onMouseEnter" to { println("eeeeenteeeeeer") },
            "onMouseLeave" to { println("leeeeeeaveeeeeee") }),

            React.createElement("div", json("style" to json("color" to "green")), "shit"))

    val swearBox1 = SwearBox1()
    val swearBox2 = SwearBox2()
    val swearBox3 = SwearBox3()
}


@native val React: IReact = noImpl

@native interface IReact {
    fun createElement(tagOrClass: dynamic, attrs: dynamic, vararg elements: dynamic): ReactElement
    fun createClass(def: dynamic): ReactClass
}

@native interface ReactElement
@native interface ReactClass

@native interface Context {
    val uit: UITools
}

var uit: UITools

@native interface UITools {
    val isInTestScenario: Boolean
    val worldIsHalted: Boolean
}

class JSUnsupportedOperationException(message: String) : Throwable(message) {
    override val message = message
    val stack = js("Error().stack")
}

fun SwearBox1(): ReactElement {
    fun renderCtor(update: ElementUpdater): () -> ReactElement {
        var phraseIndex = -1
        val phrases = arrayOf("Shit", "Fuck", "Bitch")

        fun render(): ReactElement {
            return React.createElement("div", json(),
                    if (phraseIndex == -1)
                        React.createElement("div", json("style" to json("color" to "rosybrown")), "Kittens")
                    else
                        React.createElement("div", json(),
                                React.createElement("span", json("style" to json("fontWeight" to "bold")), "Back to earth:"),
                                React.createElement("div", json(), phrases[phraseIndex])),

                    React.createElement("button", json("onClick" to fun() {
                        if (++phraseIndex > phrases.lastIndex) {
                            phraseIndex = -1
                        }
                        update(null)
                    }), "More"))
        }

        return ::render
    }

    return updatableElement(::renderCtor)
}

abstract class UpdatableElement {
    abstract fun render(): ReactElement

    var inst: dynamic = null

    val element = React.createElement(React.createClass(json(
            "componentDidMount" to fun() {
                inst = js("this")
            },

            "componentWillUnmount" to fun() {
                inst = null
            },

            "render" to fun(): ReactElement {
                return render()
            }
    )), json())

    fun update() {
        if (uit.isInTestScenario && uit.worldIsHalted) return
        if (inst == null) return

        try {
            inst.forceUpdate()
        } catch (e: dynamic) {
            js("raiseWithMeta")(json("message" to e.message, "cause" to e))
        }
    }
}

class SwearBox2 : UpdatableElement() {
    var phraseIndex = -1
    val phrases = arrayOf("Shit 2", "Fuck 2", "Bitch 2")

    override fun render() = React.createElement("div", json(),
            if (phraseIndex == -1)
                React.createElement("div", json("style" to json("color" to "rosybrown")), "Kittens 2")
            else
                React.createElement("div", json(),
                        React.createElement("span", json("style" to json("fontWeight" to "bold")), "Back to earth:"),
                        React.createElement("div", json(), phrases[phraseIndex])),

            React.createElement("button", json("onClick" to fun() {
                if (++phraseIndex > phrases.lastIndex) {
                    phraseIndex = -1
                }
                update()
            }), "More"))
}

interface ReactElementBuilder {
    fun toReactElement(): ReactElement

}

typealias ReactEventHandler = (ReactEvent) -> Unit

@native interface ReactEvent {
    fun preventDefault()
    fun stopPropagation()
}

fun preventAndStop(e: ReactEvent) {
    e.preventDefault()
    e.stopPropagation()
}

val div = makeElementCtor("div")
val span = makeElementCtor("span")

fun makeElementCtor(tag: String, container: MutableList<ReactElement>? = null): (ElementBuilder.() -> Unit) -> ReactElement {
    return fun(build: ElementBuilder.() -> Unit): ReactElement {
        val builder = ElementBuilder(tag)
        builder.build()
        val el = builder.toReactElement()
        container?.add(el)
        return el
    }
}

class Style {
    var color: Color? = null
    var fontWeight: FontWeight? = null
}

class ElementBuilder(val tag: String) {
    val children = mutableListOf<ReactElement>()
    var id: String? = null
    var onClickHandler: ReactEventHandler? = null
    val style = Style()

    fun style(build: Style.() -> Unit) {
        style.build()
    }

    fun text(s: String) {
        children.add(React.createElement("span", js("({})"), s))
    }

    val div = makeElementCtor("div", children)
    val span = makeElementCtor("div", children)

    fun toReactElement(): ReactElement {
        val jsAttrs = js("({})")

        val jsStyle = js("({})")
        jsAttrs.style = jsStyle
        jsStyle.color = style.color?.css
        jsStyle.fontWeight = style.fontWeight?.css

        if (onClickHandler != null) jsAttrs.onClick = onClickHandler

        return React.createElement(tag, jsAttrs, *children.toTypedArray())
    }

    fun onClick(handler: ReactEventHandler) {
        this.onClickHandler = handler
    }
}

fun ElementBuilder.button(title: String, onClick: ReactEventHandler): ReactElement {
    return makeElementCtor("button", children)() {
        text(title)
        this.onClick {e->
            preventAndStop(e)
            onClick(e)
        }
    }
}

class SwearBox3 : UpdatableElement() {
    var phraseIndex = -1
    val phrases = arrayOf("Shit 3", "Fuck 3", "Bitch 3")

    override fun render() = div {id="qweqwe"
        if (phraseIndex == -1)
            div {style {color=ROSYBROWN}; text("Kittens 3")}
        else
            div {
                span {style {fontWeight=BOLD}; text("Back to earth:")}
                div {text(phrases[phraseIndex])}
            }

        button("More") {
            if (++phraseIndex > phrases.lastIndex) {
                phraseIndex = -1
            }
            update()
        }
    }
}

fun updatableElement(renderCtor: RenderCtor): ReactElement {
    var inst: dynamic = null

    fun update(then: OnElementUpdated) {
        if (uit.isInTestScenario && uit.worldIsHalted) return
        if (inst == null) return

        try {
            inst.forceUpdate()
        } catch (e: dynamic) {
            js("raiseWithMeta")(json("message" to e.message, "cause" to e))
        }

        if (then != null) then()
    }

    val renderElement = renderCtor(::update)

    return React.createElement(React.createClass(json(
            "componentDidMount" to fun() {
                inst = js("this")
            },

            "componentWillUnmount" to fun() {
                inst = undefined
            },

            "render" to fun(): ReactElement {
                return renderElement()
            }
    )), json())
}


typealias ReactElementProducer = () -> ReactElement
typealias ElementUpdater = (OnElementUpdated) -> Unit
typealias OnElementUpdated = (() -> Unit)?
typealias RenderCtor = (ElementUpdater) -> ReactElementProducer


