package aps.front

import aps.*
import aps.const.text.symbols.nbsp
import kotlin.browser.window

class DynamicFooter(val world: World) : Control2(Attrs()) {
    var backendVersion: String? = null

    var burger: BurgerDropdownButton? = null

    val versions = Control2.from {kdiv{o->
        o- "Frontend: ${Globus.version}"
        backendVersion?.let {
            o- (nbsp+nbsp+nbsp)
            o- "Backend: $it"
        }
    }}

    override fun render(): ToReactElementable {
        return hor2(position = "absolute", right = 0, top = 0, fontSize = "12px", marginTop = 5, marginRight = 5){o->
            o- versions
            o- burger
        }
    }

    fun setBackendVersion(value: String) {
        backendVersion = value
        versions.update()
    }

    fun openBurger() {
        burger?.open()
    }

    fun setBurgerMenu(menu: Menu?) {
        burger =
            if (menu == null) null
            else BurgerDropdownButton(
                buttonStyle = Style(
                    border = "none",
                    backgroundColor = "transparent",
                    padding = "0px",
                    position = "relative",
                    top = -1),
                direction = BurgerDropdownButton.Direction.UP,
                menu = menu)
        update()
    }
}

