@file:Suppress("RemoveEmptyParenthesesFromLambdaCall")

package aps.front

import aps.*
import aps.RedisLogMessage.*
import aps.RedisLogMessage.Separator.Type.*
import aps.front.Color.*
import into.kommon.*

// TODO:vgrechka Typed urlQuery

class DebugPage(val ui: World) {
    val noise = DebugNoise("DebugPage", mute = false)
    val CN_ROW = "cn" + puid()
    val CN_OPAQUE = "cn" + puid()

    fun load(): Promise<Unit> = async {
        val page = when (ui.urlQuery["page"]) {
            "log" -> {
                val jsons = run {
                    val ids = await(fedis.lrange("log", 0, -1))
                    if (ids.isNotEmpty())
                        await(fedis.mget(ids))
                    else
                        listOf()
                }

                val allItems = jsons.filterNotNull().map {dejsonizeValue<RedisLogMessage>(JSON.parse(it))!!}

                Page(
                    header = pageHeader2("Fucking Log"),
                    body = kdiv{o->
                        o- rawHTML("""
                            <style>
                                .$CN_ROW {background-color: $WHITE;}
                                .$CN_ROW .$CN_OPAQUE {background-color: $WHITE;}
                                .$CN_ROW:hover {background-color: $BLUE_GRAY_50;}
                                .$CN_ROW:hover .$CN_OPAQUE {background-color: $BLUE_GRAY_50;}
                            </style>
                        """)

                        noise.clog("allItems.size", allItems.size)

                        var itemsToRender = mutableListOf<RedisLogMessage>()
                        for (item in allItems) {
                            itemsToRender.add(item)
                        }

                        fun cutFromLastBoot(bottomIndex: Int) {
                            for (i in bottomIndex downTo 0) {
                                val item = itemsToRender[i]
                                if (item is Separator && item.type == THICK_SEPARATOR) {
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
                                fun renderMsgText() = kdiv(whiteSpace="pre"){o->
                                    o- msg.text
                                }

                                kdiv(position = "relative", className = CN_ROW){o->
                                    exhaustive/when (msg) {
                                        is Separator -> {
                                            when (msg.type) {
                                                SEPARATOR -> renderSeparator(o, msg.text, "1px solid $BLUE_500", "0.75em")
                                                THICK_SEPARATOR -> renderSeparator(o, msg.text, "5px solid $BLUE_500", "0.55em")
                                                THICK_DASHED_SEPARATOR -> renderSeparator(o, msg.text, "5px dashed $BLUE_500", "0.55em")
                                            }
                                        }
                                        is SQL -> {
                                            val short = msg.shortDescription
                                            if (short != null)
                                                o- Betsy("SQL: $short", renderMsgText())
                                            else
                                                o- renderMsgText()
                                        }
                                    }
                                    o- kdiv(className = CN_OPAQUE, position="absolute", top=0, right=0, paddingLeft="0.5em"){o->
                                        o- showRawStackLink(msg.stack, msg.stamp)
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
    }

    private fun renderSeparator(o: ElementBuilder, title: String, borderStyle: String, offset: String) {
        o - kdiv(borderBottom = borderStyle, position = "absolute", top = offset, width = "100%")
        o - kdiv(position = "relative", left = "1em"){o->
            o - kspan(className = CN_OPAQUE, paddingLeft = "0.5em", paddingRight = "0.5em"){o->
                o - title
            }
        }
    }
}




