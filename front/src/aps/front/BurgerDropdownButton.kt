package aps.front

import aps.*
import aps.front.FAIcon.*
import org.w3c.dom.events.MouseEvent

class BurgerDropdownButton(val menu: Menu, val icon: FAIcon = BARS) : Control2(Attrs()) {
    val menuID = puid()

    override fun render(): ToReactElementable {
        return ToReactElementable.from(
            reactCreateElement("div", json("className" to "dropdown"), listOf(
                reactCreateElement("button", json(
                    "className" to "btn btn-default dropdown-toggle",
                    "type" to "button",
                    "id" to menuID,
                    "data-toggle" to "dropdown"
                ),listOf(
                    reactCreateElement("i", json("className" to "fa $icon"), listOf())
                )),
                reactCreateElement("ul", json(
                    "className" to "dropdown-menu dropdown-menu-right",
                    "style" to json("min-width" to 100)
                ), menu.items.map {item->
                    reactCreateElement("li", json(), listOf(
                        reactCreateElement("a", json(
                            "href" to "#",
                            "onClick" to {e: MouseEvent ->
                                preventAndStop(e)
                                item.act()
                            }
                        ), listOf(
                            item.title.asReactElement()
                        ))
                    ))
                })
            ))
        )
    }
}



