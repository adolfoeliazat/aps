/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@native
public val React: IReact = noImpl

@native
public interface IReact {
    fun  createElement(tag: String, attrs: dynamic, vararg elements: dynamic): Any
}

class Style(var border: String? = null, var background: String? = null, var fontWeight: String? = null) {
}

fun renderSwearBox(): Any {
    return diva(style=Style(border="2px solid red"), items=arrayOf(
        diva(style=Style(background ="lightgreen", fontWeight = "bold"), items=arrayOf("fucking")),
        diva(style=Style(background ="lightblue"), items=arrayOf("shit"))
    ))
}

fun diva(style: Style?, items: Array<Any>): Any {
    val attrs: dynamic = js("({})")

    if (style !== null) {
        val s = js("({})")
        attrs.style = s
        s.border = style.border
        s.background = style.background
        s.fontWeight = style.fontWeight
    }

    return React.createElement("div", attrs, *items)
}



