package aps.front

import aps.*
import aps.front.FAIcon.*
import org.w3c.dom.events.MouseEvent
import kotlin.js.json

class BurgerDropdownButton(
    val menu: Menu,
    val icon: FAIcon = BARS,
    val buttonStyle: Style = Style(),
    val direction: Direction = Direction.DOWN
) : Control2(Attrs()) {

    enum class Direction(val string: String) {
        DOWN("down"),
        UP("up")
    }

    val buttonID = puid()

    override fun render(): ToReactElementable {
        return ToReactElementable.from(
            reactCreateElement("div", json("className" to "drop${direction.string}"), listOf(
                reactCreateElement("button", json(
                    "id" to buttonID,
                    "className" to "btn btn-default dropdown-toggle",
                    "type" to "button",
                    "data-toggle" to "dropdown",
                    "style" to buttonStyle.toReactStyle()
                ),listOf(
                    reactCreateElement("i", json("className" to "fa $icon"), listOf())
                )),
                reactCreateElement("ul", json(
                    "className" to "dropdown-menu dropdown-menu-right",
                    "style" to json("minWidth" to 100)
                ), menu.items.map {item->
                    reactCreateElement("li", json(), listOf(
                        reactCreateElement("a", json(
                            "href" to "#",
                            "onClick" to {e: MouseEvent -> async {
                                preventAndStop(e)
                                item.act()
                            }}
                        ), listOf(
                            item.title.asReactElement()
                        ))
                    ))
                })
            ))
        )
    }

    fun open() {
        byid(buttonID).click()
    }
}



