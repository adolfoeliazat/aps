package aps.front

import aps.*
import into.kommon.*

fun highlightedShit(text: String, ranges: List<IntRangeRTO>, backgroundColor: Color = Color.AMBER_200): ToReactElementable {
    return highlightedShit(text, ranges.map {it.start..it.endInclusive}, backgroundColor)
}

fun highlightedShit(text: String, ranges: List<IntRange>, backgroundColor: Color = Color.AMBER_200): ToReactElementable {
    return rawHTML(highlightedHTML(text, ranges, backgroundColor), "p").toToReactElementable()
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

