/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.js.json

class UI2(val lus: World) {
    fun setPage(header: ToReactElementable,
                body: ToReactElementable,
                headerControls: ToReactElementable? = null,
                onKeyDown: ((KeyboardEvent) -> Unit)? = null
    ) {
        jqbody.scrollTop(0)
        lus.setRootContent(Shitus.updatableElement(json(), elementCtor@{update: dynamic ->
            lus.updatePage = update
            return@elementCtor {Shitus.diva(json("className" to "container", "style" to json("position" to "relative"), "onKeyDown" to onKeyDown),
                Shitus.updatableElement(json(), {update: dynamic ->
                    lus.updatePageHeader = update
                    {
                        Shitus.diva(json(),
                            header.toReactElement(),
                            headerControls?.let {Shitus.diva(json("style" to json("position" to "absolute", "right" to 14, "top" to 16)), it.toReactElement())}
                        )
                    }
                }),
                body.toReactElement()
            )}
        }))

//        lus.currentPage = json(
//            "header" to header,
//            "headerControls" to headerControls,
//            "body" to body,
//            "onKeyDown" to onKeyDown)

    }
}

