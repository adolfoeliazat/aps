package aps.front

import aps.*
import into.kommon.*
import kotlin.js.json

fun highlightedShit(text: String, ranges: List<IntRangeRTO>, backgroundColor: Color? = null, tag: String? = null, style: Style = Style()): ToReactElementable {
    return highlightedShit(text, ranges.map {it.start..it.endInclusive}, backgroundColor, tag, style = style)
}

fun highlightedShit(text: String, ranges: List<IntRange>, backgroundColor: Color? = null, tag: String? = null, style: Style = Style()): ToReactElementable {
    return rawHTML(highlightedHTML(text, ranges, backgroundColor ?: Color.AMBER_200), tag ?: "p", style = style).toToReactElementable()
}

fun highlightedHTML(text: String, ranges: List<IntRange>, backgroundColor: Color): String {
    val noise = DebugNoise(::highlightedHTML.name, mute = true)

    for ((i, range) in ranges.withIndex()) {
        noise.clog("Range $i: [" + text.substring(range) + "]")
    }

    val markedText = buildString {
        for ((i, c) in text.withIndex()) {
            if (ranges.any {it.start == i})
                append("\\{")
            if (c == '\\')
                append("\\")
            append(c)
            if (ranges.any {it.endInclusive == i})
                append("\\}")
        }
    }

    var html = ReactDOMServer.renderToStaticMarkup(reactCreateElement("p", json(), listOf(markedText.asReactElement())))
    check(html.startsWith("<p>") && html.endsWith("</p>"))
    html = html.substring("<p>".length, html.length - "</p>".length)

    return buildString {
        var i = 0
        while (i < html.length) {
            if (html[i] == '\\') {
                check(i + 1 < html.length)
                append(
                    when (html[i + 1]) {
                        '\\' -> '\\'
                        '{' -> "<span style='background-color: $backgroundColor;'>"
                        '}' -> "</span>"
                        else -> wtf("Bad escape sequence: \\${html[i + 1]}")
                    })
                ++i
            } else {
                append(html[i])
            }
            ++i
        }
    }
}

