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
    val MAX_LEN = 200L
    val CN_ROW = "cn" + puid()
    val CN_OPAQUE = "cn" + puid()

    fun load(): Promise<Unit> = async {
        val page = when (ui.urlQuery["page"]) {
            "log" -> {
                var rootMessages = await(getLogMessages(RedisLogMessage.ROOT_ID))
                run {
                    fun cutFromLastBoot(bottomIndex: Int) {
                        for (i in bottomIndex downTo 0) {
                            val item = rootMessages[i]
                            if (item is Separator && item.type == THICK_SEPARATOR) {
                                rootMessages = rootMessages.subList(i, rootMessages.size)
                                break
                            }
                        }
                    }

                    val cut = ui.urlQuery["cut"]
                    exhaustive/when (cut) {
                        null -> {}
                        "lastBoot" -> {
                            cutFromLastBoot(rootMessages.lastIndex)
                        }
                        "lastNonEmptyBoot" -> {
                            var bottomIndex = rootMessages.lastIndex
                            loopNotTooLong {
                                cutFromLastBoot(bottomIndex)
                                if (rootMessages.size != 1) abort()
                                // clog("not aborting", itemsToRender.toDebugString())

                                bottomIndex = rootMessages.lastIndex - 1
                                if (bottomIndex < 0) {
                                    rootMessages = listOf()
                                    abort()
                                }
                            }
                        }
                        else -> wtf("cut: $cut")
                    }
                }


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

                        o- renderLogMessages(rootMessages)
                    }
                )
            }

            else -> wtf()
        }

        KotlinShit.ui.setPage(page)
        // initFakeFeed()
    }

    fun getLogMessages(parentID: String): Promise<List<RedisLogMessage>> = async {
        val jsons = run {
            val ids = await(fedis.lrange("$parentID:children", -MAX_LEN, -1))
            if (ids.isNotEmpty())
                await(fedis.mget(ids))
            else
                listOf()
        }

        jsons.filterNotNull().map {dejsonizeValue<RedisLogMessage>(JSON.parse(it))!!}
    }

    fun renderLogMessages(messages: List<RedisLogMessage>): ToReactElementable {
        if (messages.isEmpty()) return kdiv {o->
            o- "No shit to render..."
        }

        return kdiv {o->
            o+ messages.map {msg->
                fun renderMsgText() = kdiv(whiteSpace = "pre", fontFamily = "monospace"){o->
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

                        is Fuck -> {
                            var virgin = true

                            val content = Placeholder(kdiv(fontStyle = "italic"){o->
                                o- "Loading..."
                            })

                            o- Betsy(msg.text, content, onExpand = {
                                if (virgin) {
                                    virgin = false
                                    async<Unit> {
                                        val children = await(getLogMessages(msg.id))
                                        content.setContent(renderLogMessages(children))
                                        content.update()
                                    }
                                }
                            })
                        }

                        is SQL -> {
                            val short = msg.shortDescription
                            if (short != null) {
                                val elapsed = msg.endMillis?.let {
                                    nbsp+nbsp+nbsp + (it - msg.beginMillis) + "ms"
                                } ?: ""

                                o- Betsy("SQL: $short $elapsed", renderMsgText())
                            }
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

    fun renderSeparator(o: ElementBuilder, title: String, borderStyle: String, offset: String) {
        o - kdiv(borderBottom = borderStyle, position = "absolute", top = offset, width = "100%")
        o - kdiv(position = "relative", left = "1em"){o->
            o - kspan(className = CN_OPAQUE, paddingLeft = "0.5em", paddingRight = "0.5em"){o->
                o - title
            }
        }
    }
}




