/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

fun jsFacing_button(def: dynamic): dynamic {
    val title: dynamic = def.title
    val hint: dynamic = def.hint
    val level: dynamic = if (def.level != null) def.level else "default"
    val icon: dynamic = def.icon
    val iconColor: dynamic = def.iconColor
    val onClick: dynamic = def.onClick
    val className: dynamic = if (def.className != null) def.className else ""
    val style: dynamic = def.style

    var me: dynamic = null
    me = json(
        "render" to render@{
            val glyphAttrs: dynamic = json()
            if (iconColor) {
                global.Object.assign(glyphAttrs, json("style" to json("color" to iconColor)))
            }

            return@render React.createElement("button", global.Object.assign(def, json(
                "id" to me.elementID,
                "className" to "btn btn-${level} ${className}",
                "style" to style,
                "title" to if (hint != null) textMeat(hint) else null)),

                if (icon != null) Shitus.glyph(icon, glyphAttrs) else null,
                if (icon != null && title != null) Shitus.spana(json(), nbsp) else null,
                Shitus.spana(json(), title)
            )
        },

        "onRootClick" to {e: dynamic -> "__async"
            e.preventDefault()
            e.stopPropagation()
            if (onClick) __await<dynamic>(onClick())
            // await fova(onClick)
        },

        "contributeTestState" to {state: dynamic ->
            if (me.tame) {
                if (title) state.put(json("control" to me, "key" to me.getTamePath() + ".title", "value" to textMeat(title)))
                if (icon) state.put(json("control" to me, "key" to me.getTamePath() + ".icon", "value" to textMeat(icon)))
            }
        }
    )

    me.controlTypeName = "button"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to onClick)))

    return elcl(me)
}

