/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import Color.*
import FontWeight.*

@native val React: IReact = noImpl
@native interface ReactElement
@native interface ReactClass

@native interface IReact {
    fun createElement(tagOrClass: dynamic, attrs: dynamic, vararg elements: dynamic): ReactElement
    fun createClass(def: dynamic): ReactClass
}

var uit: UITools

@native interface UITools {
    val isInTestScenario: Boolean
    val worldIsHalted: Boolean
}

fun makeTestShit() = object {
    val swearBox3 = SwearBox3()
}

class SwearBox3 : UpdatableElement() {
    var phraseIndex = -1
    val phrases = arrayOf("Shit 3", "Fuck 3", "Bitch 3")

    override fun render() = div {id="someid"
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

enum class Color(val css: String) {
    RED("red"),
    GREEN("green"),
    BLUE("blue"),
    ROSYBROWN("rosybrown")
}

enum class FontWeight(val css: String) {
    BOLD("bold")
}

class JSUnsupportedOperationException(message: String) : Throwable(message) {
    override val message = message
    val stack = js("Error().stack")
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


