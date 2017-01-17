package aps.front

import jquery.jq

private var _crossWorld: CrossWorld? = null

val crossWorld: CrossWorld
    get() {
        if (_crossWorld == null) _crossWorld = CrossWorld()
        return _crossWorld!!
    }

class CrossWorld {
    val locationControl = Control2.from {kdiv(className = css.test.crossWorld.locationPane){o->
        o- kspan(className = css.test.crossWorld.label){o->
            var text = ""
            Globus.worldMaybe?.let {
                text += it.name + " :: "
            }
            text += Globus.location.href

            o- text
        }
    }}

    val locationPane = debugPanes.put(locationControl)

    init {
        jq("#footer > div > .container").hide()
    }
}

fun killEverythingVisual() {
    panes.removeAll()
    debugPanes.removeAll()
    DOMReact.containers.toList().forEach {DOMReact.unmountComponentAtNode(it)}
    _crossWorld = null
}



