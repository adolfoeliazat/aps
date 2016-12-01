package aps.front

import aps.front.Color.*

class Betsy(
    val title: String,
    val content: ToReactElementable,
    val onExpand: () -> Unit = {},
    val className: String = "",
    val headerClassName: String = "",
    val renderInHeader: (ElementBuilder) -> Unit = {}
) : Control2(Attrs()) {
    private var collapsed = true

    override fun render() = kdiv(className = className){o->
        o- kdiv(className = headerClassName){o->
            o- ki(className = "fa fa-${if (collapsed) "plus" else "minus"}-square", cursor = "pointer", onClick={onClick()})
            o- kspan(marginLeft = "0.5em", fontWeight = "bold"){o->
                o- title
            }
            renderInHeader(o)
        }

        if (!collapsed)
            o- kdiv(marginLeft = "0.4em", paddingLeft = "1em", borderLeft = "1px dashed $GRAY_500"){o->
                o- content
            }
    }

    private fun onClick() {
        collapsed = !collapsed
        update()

        if (!collapsed) onExpand()
    }
}




