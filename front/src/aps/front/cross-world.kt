package aps.front

import into.kommon.*
import jquery.jq
import kotlin.properties.Delegates.notNull

private var _crossWorld: CrossWorld? = null

val crossWorld: CrossWorld
    get() {
        if (_crossWorld == null) _crossWorld = CrossWorld()
        return _crossWorld!!
    }

class CrossWorld {
    private val locationControl = Control2.from {kdiv(className = css.test.crossWorld.locationPane){o->
        o- kspan(className = css.test.crossWorld.label){o->
            var text = ""
            text += Browseroid.current.name + " :: "
            text += Globus.location.href

            o- text
        }
    }}

    private var pane: String? = null

    fun createOrUpdate() {
        if (pane == null) {
            pane = old_debugPanes.put(locationControl)
            locationControl.update()
        } else {
            locationControl.update()
        }
    }

    fun dispose() {
        pane?.let {
            old_debugPanes.remove(it)
            pane = null
        }
    }
}

fun killEverythingVisual() {
//    old_panes.removeAll()
//    old_debugPanes.removeAll()
//    DOMReact.containers.toList().forEach {DOMReact.unmountComponentAtNode(it)}
//    _crossWorld = null
}



