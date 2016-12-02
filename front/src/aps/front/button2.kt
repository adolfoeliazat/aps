package aps.front

import org.w3c.dom.events.MouseEvent

fun button2(tre: ToReactElementable, act: () -> Unit): ToReactElementable {
    return ToReactElementable.from(reactCreateElement("button", json(
        "className" to "btn btn-default",
        "onClick" to {e: MouseEvent ->
            preventAndStop(e)
            act()
        }
    ), listOf(
        tre.toReactElement()
    )))
}

