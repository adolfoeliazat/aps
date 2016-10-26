/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_link(def: Json): ReactElement {
    var content: dynamic = def["content"]
    val title: String? = def["title"].asDynamic()
    val className: String = (def["className"].asDynamic()) ?: ""
    val style: dynamic = def["style"] ?: json()
    val onClick: dynamic = def["onClick"]
    val onMouseEnter: dynamic = def["onMouseEnter"]
    val onMouseLeave: dynamic = def["onMouseLeave"]

    Shitus.invariant(!(content != null && title != null), "It should be either content or title")

    if (title != null) {
        content = Shitus.spancTitle(json("title" to title))
    }

    var me: dynamic = null
    me = json(
        "render" to {
            Shitus.aa(json(
                "id" to me.elementID,
                "className" to className,
                "style" to style,
                "href" to "#",
                "onMouseEnter" to onMouseEnter,
                "onMouseLeave" to onMouseLeave),
            content)
        },

        "onRootClick" to {e: dynamic -> "__async"
            e.preventDefault()
            e.stopPropagation()
            val shit: (() -> Promise<Any?>)? = onClick
            shit?.let {__await(it())}
//            __await<dynamic>(jshit.utils.fova(onClick))
        }
    )

    me.controlTypeName = "link"
    me.tamyPrefix = "link"
    legacy_implementControlShit(json("me" to me, "def" to def, "implementTestClick" to json("onClick" to me.onRootClick)))
    return elcl(me)
}

fun jsFacing_urlLink(ui: dynamic, def: dynamic): dynamic {
    val name = def.name // TODO:vgrechka Seems to be unused
    val url = def.url
    val delayActionForFanciness = def.delayActionForFanciness
    val blinkOpts = def.blinkOpts
    val style = def.style

    val id = puid()

    val linkDef: dynamic = json(
        "controlTypeName" to "urlLink",
        "style" to style,
        "id" to id,
        "onClick" to {"__async"
            effects.blinkOn(global.Object.assign(json("target" to Shitus.byid(id), "dtop" to 3), blinkOpts))
            if (name != null) {
                Shitus.byid(id).css("text-decoration", "none")
//                TestGlobal["link_" + name + "_blinks"] = true
            }

            if (delayActionForFanciness && !(isInTestScenario() && art.testSpeed == "fast")) {
                __await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
            }

            __await<dynamic>(Shitus.entraina(json("name" to "Navigate via urlLink: ${url}", "act" to {"__async"
                __await<dynamic>(ui.pushNavigate(url))
            })))

            effects.blinkOff()
            if (name) {
                Shitus.byid(id).css("text-decoration", "")
//                TestGlobal["link_" + name + "_blinks"] = false
            }
    })

    return Shitus.link(Shitus.asn1(linkDef, def))
}

fun jsFacing_pageLink(ui: dynamic, def: dynamic) {
    Shitus.raise("Kill me please, I don’t deserve living")
}

@GenerateSignatureMixes
fun urlLink(title: String,
            url: String,
            delayActionForFanciness: Boolean = false,
            blinkOpts: dynamic = null,
            @Mix attrs: Attrs = Attrs(),
            @Mix style: Style = Style()
): ToReactElementable {
    imf("urlLink")
//    val id = puid()
//
//    val linkDef: dynamic = json(
//        "controlTypeName" to "urlLink",
//        "style" to style,
//        "id" to id,
//        "onClick" to {"__async"
//            effects.blinkOn(global.Object.assign(json("target" to Shitus.byid(id), "dtop" to 3), blinkOpts))
//            if (name != null) {
//                Shitus.byid(id).css("text-decoration", "none")
////                TestGlobal["link_" + name + "_blinks"] = true
//            }
//
//            if (delayActionForFanciness && !(isInTestScenario() && art.testSpeed == "fast")) {
//                __await<dynamic>(Shitus.delay(global.ACTION_DELAY_FOR_FANCINESS))
//            }
//
//            __await<dynamic>(Shitus.entraina(json("name" to "Navigate via urlLink: ${url}", "act" to {"__async"
//                __await<dynamic>(ui.pushNavigate(url))
//            })))
//
//            effects.blinkOff()
//            if (name) {
//                Shitus.byid(id).css("text-decoration", "")
////                TestGlobal["link_" + name + "_blinks"] = false
//            }
//        })
//
//    return Shitus.link(Shitus.asn1(linkDef, def))
}











