package aps.front

import kotlin.properties.Delegates.notNull

object LocationBar {
    private var initialized = false
    private var pane by notNull<String>()
    private var control by notNull<Control2>()

    fun update() {
        if (!initialized) {
            control = Control2.from {kdiv(className = css.test.crossWorld.locationPane){o->
                o- kspan(className = css.test.crossWorld.label){o->
                    val bro = Globus.currentBrowseroid
                    o- (bro.name + " :: " + bro.location.href)
                }
            }}

            pane = old_debugPanes.put(control)
        }


        control.update()
    }

    fun dispose() {
        if (initialized) {
            old_debugPanes.remove(pane)
            initialized = false
        }
    }
}


