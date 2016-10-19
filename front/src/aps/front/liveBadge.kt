/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

fun jsFacing_liveBadge(ui: dynamic, def: dynamic): dynamic {
    imf("jsFacing_liveBadge")
//    return jshit.statefulElement(json(
//        "ctor" to ctor@{update: dynamic ->
//            var me: dynamic = null
//            me = json(
//                "render" to render@{
//                    if (!me.getValue()) return@render null
//                    // Was dom.spana
//                    return@render Shitus.spana(json("id" to def.id), Shitus.spana(json(
//                        "className" to "badge ${if (def.className) def.className else ""}",
//                        "style" to global.Object.assign(json(
//                            "marginTop" to -2,
//                            "marginLeft" to 5,
//                            "paddingRight" to 8,
//                            "backgroundColor" to "" + Color.BLUE_GRAY_400),
//                            def.style),
//                        "onClick" to me.onPhysicalClick),
//                        me.getValue()))
//                },
//
//                "getValue" to {
//                    ui.liveStatus[def.liveStatusFieldName]
//                },
//
//                "componentDidMount" to {
//                    ui.liveStatusUpdaters[def.name] = update
//                },
//
//                "componentWillUnmount" to {
//                    jshit.utils.deleteKey(ui.liveStatusUpdaters, def.name)
//                }
//            )
//
//            implementControlTestFacilities(json("me" to me, "def" to def, "controlTypeName" to "liveBadge"))
//            return@ctor me
//        }
//    ))
}

fun jsFacing_liveBadge2(ui: dynamic, def: dynamic): dynamic {
    if (def.tame == null) def.tame = "badge"

    return Shitus.statefulElement(json(
        "ctor" to ctor@{update: dynamic ->

            fun getValue(): dynamic {
                return ui.liveStatus[def.liveStatusFieldName]
            }

            var me: dynamic = null
            me = json(
                "render" to render@{
                    if (!getValue()) return@render null
                    Shitus.spana(json("id" to me.elementID, "className" to "badge",
                        "style" to global.Object.assign(json(
                            "marginTop" to -2,
                            "marginLeft" to 5,
                            "paddingRight" to 8,
                            "backgroundColor" to "" + Color.BLUE_GRAY_400),
                            def.style)),
                        getValue())
                },

                "componentDidMount" to {
                    ui.liveStatusUpdaters[def.name] = update
                },

                "componentWillUnmount" to {
                    jsFacing_deleteKey(ui.liveStatusUpdaters, def.name)
                },

                "contributeTestState" to {state: dynamic ->
                    if (getValue()) {
                        state.put(json("control" to me, "key" to me.getTamePath(), "value" to textMeat(getValue())))
                    }
                },

                "getLongRevelationTitle" to {
                    me.debugDisplayName + ": " + textMeat(getValue())
                }
            )

            me.controlTypeName = "liveBadge2"
            me.useControlTypeNameAsDefaultTame = true

            legacy_implementControlShit(json("me" to me, "def" to def))
            return@ctor me
        }
    ))
}










