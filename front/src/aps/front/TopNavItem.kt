/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.events.KeyboardEvent

fun jsFacing_TopNavItem(def: dynamic): dynamic {
    val title: dynamic = def.title
    val liveStatusFieldName: dynamic = def.liveStatusFieldName
    val active: dynamic = def.active
    val ui: World = def.ui
    val aStyle: dynamic = def.aStyle

    val href = if (def.name == "home") "/" else "${def.name}.html"
    val aid = puid()

    var me: dynamic = null
    me = json(
        "render" to {
            Shitus.lia(json("id" to me.elementID, "className" to (if (active) "active" else "")),
                Shitus.aa(json("id" to aid, "href" to href, "style" to aStyle),
                    Shitus.spancTitle(json("title" to title))
//                    if (liveStatusFieldName != null) ui.liveBadge2(json("liveStatusFieldName" to liveStatusFieldName)) else null
                ))
        },

        "onRootClick" to {e: KeyboardEvent -> "__async"
            preventAndStop(e)

            var dleft = 0; var dwidth = 0
            if (def.name == "home") { // XXX For some reason jQuery cannot find width/offset of navbar-header element precisely
                // @revisit
                dleft = -15
                dwidth = 15
            }

            effects.blinkOn(json("target" to Shitus.byid(aid).parent(), "fixed" to true, "dleft" to dleft, "dwidth" to dwidth))

            __await<dynamic>(ui.pushNavigate(href))

            global.setTimeout({
                effects.blinkOff()
                global.bsClearMenus()
            }, 250)
        }
    )

    me.controlTypeName = "TopNavItem"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to me.onRootClick)))

    return elcl(me)
}

