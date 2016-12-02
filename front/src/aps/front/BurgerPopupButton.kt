package aps.front

import aps.*
import aps.front.FAIcon.*

class BurgerPopupButton(val menu: Menu, val icon: FAIcon = BARS) : Control2(Attrs()) {
    override fun render(): ToReactElementable {
        return button2(ki(className = "fa $icon")) {
            clog("aaaaaaaaaaaa")
        }
    }
}



