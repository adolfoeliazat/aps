package aps

import aps.front.*
import kotlin.js.json

class Emoji(val hex: String) {
    fun render(style: Style = Style(), size: Int = 18): ReactElement {
        return reactCreateElement("img", json(
            "src" to "https://twemoji.maxcdn.com/2/svg/$hex.svg",
            "width" to size, "height" to size,
            "style" to style.toReactStyle()
        ))
    }
}

