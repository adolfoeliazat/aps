package aps

import aps.front.*
import kotlin.js.json

interface XIcon {
    fun render0(style: Style, size: Int): ReactElement
}

fun XIcon.render(style: Style = Style(), size: Int = 18) =
    render0(style, size)

class Twemoji(val hex: String) : XIcon {
    override fun render0(style: Style, size: Int): ReactElement {
        return reactCreateElement("img", json(
            "src" to "https://twemoji.maxcdn.com/2/svg/$hex.svg",
            "width" to size, "height" to size,
            "style" to style.toReactStyle()
        ))
    }
}

class EmojiOne(val hex: String) : XIcon {
    override fun render0(style: Style, size: Int): ReactElement {
        return reactCreateElement("img", json(
            "src" to "https://cdnjs.cloudflare.com/ajax/libs/emojione/2.2.7/assets/svg/$hex.svg",
            "width" to size, "height" to size,
            "style" to style.toReactStyle()
        ))
    }
}

