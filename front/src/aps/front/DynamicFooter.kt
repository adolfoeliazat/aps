package aps.front

import aps.*

class DynamicFooter(val world: World) : Control2(Attrs()) {
    var backendVersion: String? = null

    override fun render(): ToReactElementable {
        return kdiv(position = "absolute", right = 0, top = 0, fontSize = "12px", marginTop = 5, marginRight = 5){o->
            o- "Frontend: ${Globus.version}"
            backendVersion?.let {
                o- (nbsp+nbsp+nbsp)
                o- "Backend: $it"
            }
        }
    }

    fun setBackendVersion(value: String) {
        backendVersion = value
        update()
    }
}

