package aps.front

import aps.*
import aps.RedisLogMessage.Type.*
import aps.front.Color.*
import into.kommon.*

// TODO:vgrechka Typed urlQuery

class DebugPage(val ui: World) {
    val noise = DebugNoise("DebugPage", mute = false)

    fun load(): Promise<Unit> {"__async"
        val page = when (ui.urlQuery["page"]) {
            "log" -> {
                val allItems = __await(GetRedisLogMessagesRequest().send()).items
                Page(
                    header = pageHeader2("Fucking Log"),
                    body = kdiv{o->
                        noise.clog("allItems.size", allItems.size)

                        var itemsToRender = mutableListOf<RedisLogMessage>()
                        for (item in allItems) {
                            itemsToRender.add(item)
                        }

                        fun cutFromLastBoot(bottomIndex: Int) {
                            for (i in bottomIndex downTo 0) {
                                if (itemsToRender[i].type == THICK_SEPARATOR) { // TODO:vgrechka Introduce BOOT type
                                    itemsToRender = itemsToRender.subList(i, itemsToRender.size)
                                    break
                                }
                            }
                        }

                        val cut = ui.urlQuery["cut"]
                        exhaustive/when (cut) {
                            null -> {}
                            "lastBoot" -> {
                                cutFromLastBoot(itemsToRender.lastIndex)
                            }
                            "lastNonEmptyBoot" -> {
                                var bottomIndex = itemsToRender.lastIndex
                                loopNotTooLong {
                                    cutFromLastBoot(bottomIndex)
                                    if (itemsToRender.size != 1) abort()
                                    // clog("not aborting", itemsToRender.toDebugString())

                                    bottomIndex = itemsToRender.lastIndex - 1
                                    if (bottomIndex < 0) {
                                        itemsToRender = mutableListOf()
                                        abort()
                                    }
                                }
                            }
                            else -> wtf("cut: $cut")
                        }

                        if (itemsToRender.isEmpty()) {
                            o- "No shit to render..."
                        } else {
                            o+ itemsToRender.map {msg->
                                kdiv(position="relative"){o->
                                    exhaustive/when (msg.type) {
                                        SEPARATOR -> renderSeparator(o, msg.text, "1px solid $BLUE_500", "0.75em")
                                        THICK_SEPARATOR -> renderSeparator(o, msg.text, "5px solid $BLUE_500", "0.55em")
                                        SQL ->
                                            o- kdiv(whiteSpace="pre"){o->
                                                o- msg.text
                                            }
                                    }
                                    o- kdiv(position="absolute", top=0, right=0, backgroundColor=WHITE, paddingLeft="0.5em"){o->
                                        o- msg.stamp
                                    }
                                }
                            }
                        }
                    }
                )
            }

            else -> wtf()
        }

        KotlinShit.ui.setPage(page)
        // initFakeFeed()
        return __asyncResult(Unit)
    }

    private fun renderSeparator(o: ElementBuilder, title: String, borderStyle: String, offset: String) {
        o - kdiv(borderBottom = borderStyle, position = "absolute", top = offset, width = "100%")
        o - kdiv(position = "relative", left = "1em"){o->
            o - kspan(backgroundColor = WHITE, paddingLeft = "0.5em", paddingRight = "0.5em"){o->
                o - title
            }
        }
    }
}




