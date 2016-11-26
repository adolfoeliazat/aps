package aps.front

import aps.front.Color.*

class Betsy(val title: String, val content: ToReactElementable) : Control2(Attrs()) {
    private var collapsed = true

    override fun render() = kdiv{o->
        o- ki(className = "fa fa-${if (collapsed) "plus" else "minus"}-square", cursor = "pointer", onClick={onClick()})
        o- kspan(marginLeft = "0.5em", fontWeight = "bold"){o->
            o- title
        }

        if (!collapsed)
            o- kdiv(marginLeft = "0.4em", paddingLeft = "1em", fontFamily = "monospace", borderLeft = "1px dashed $GRAY_500"){o->
                o- content
            }
    }

    private fun onClick() {
        collapsed = !collapsed
        update()
    }
}




