package aps.front

import aps.*
//import aps.RedisLogMessage.*
//import aps.RedisLogMessage.Separator.Type.*
import aps.Color.*
import aps.const.text.symbols.nbsp
import into.kommon.*
import kotlin.js.json

class DebugLogPage(val world: World) {
//    val noise = DebugNoise("DebugPage", mute = false)
//    val MAX_LEN = 200L
//    val CN_ROW = "cn" + puid()
//    val CN_HOVER_HIGHLIGHT = "cn" + puid()
//    val CN_OPAQUE = "cn" + puid()
//
//    enum class Cut {NONE, LAST_BOOT, LAST_NON_EMPTY_BOOT}
//
//    inner class URLQuery : URLQueryBase_killme(world) {
//        val cut by enumURLParam_killme(Cut.values(), Cut.NONE)
//        val skipCrap by booleanURLParam_killme()
//    }
//
//    val urlQuery = URLQuery()

    suspend fun load() {
        imf("Revisit    e65fb8da-8b35-4be7-942c-c7e9e1ddc32d")

//        var rootMessages = await(getLogMessages(RedisLogMessage.ROOT_ID))
//        run {
//            fun cutFromLastBoot(bottomIndex: Int) {
//                for (i in bottomIndex downTo 0) {
//                    val item = rootMessages[i]
//                    if (item is Separator && item.type == THICK_SEPARATOR) {
//                        rootMessages = rootMessages.subList(i, rootMessages.size)
//                        break
//                    }
//                }
//            }
//
//            exhaustive=when (urlQuery.cut) {
//                Cut.NONE -> {}
//                Cut.LAST_BOOT -> {
//                    cutFromLastBoot(rootMessages.lastIndex)
//                }
//                Cut.LAST_NON_EMPTY_BOOT -> {
//                    var bottomIndex = rootMessages.lastIndex
//                    loopNotTooLong {
//                        cutFromLastBoot(bottomIndex)
//                        if (rootMessages.size != 1) abort()
//                        // clog("not aborting", itemsToRender.toDebugString())
//
//                        bottomIndex = rootMessages.lastIndex - 1
//                        if (bottomIndex < 0) {
//                            rootMessages = listOf()
//                            abort()
//                        }
//                    }
//                }
//            }
//        }
//
//        world.setPage(Page(
//            header = pageHeader2("Fucking Log"),
//
//            headerControls = BurgerDropdownButton(Menu(listOf(
//                MenuItem("Clear") {async{
//                    clog("Clearing all fucking shit")
////                    await(fedis.del(listOf("${RedisLogMessage.ROOT_ID}:children")))
//                    Globus.realLocation.reload()
//                }}
//            ))),
//
//            body = kdiv{o->
//                o- rawHTML("""
//                            <style>
//                                .$CN_HOVER_HIGHLIGHT {background-color: $WHITE;}
//                                .$CN_HOVER_HIGHLIGHT .$CN_OPAQUE {background-color: $WHITE;}
//                                .$CN_HOVER_HIGHLIGHT:hover {background-color: $BLUE_GRAY_50;}
//                                .$CN_HOVER_HIGHLIGHT:hover .$CN_OPAQUE {background-color: $BLUE_GRAY_50;}
//                            </style>
//                        """)
//
//                o- renderLogMessages(rootMessages)
//            }
//        ))
    }

//    fun getLogMessages(parentID: String): Promisoid<List<RedisLogMessage>> = async {
//        imf("3fd6e11b-06e7-4abe-a885-381dd6580b7b")
////        val jsons = run {
////            val ids = await(fedis.lrange("$parentID:children", -MAX_LEN, -1))
////            if (ids.isNotEmpty())
////                await(fedis.mget(ids))
////            else
////                listOf()
////        }
////
////        jsons.filterNotNull().map {dejsonizeValue<RedisLogMessage>(JSON.parse(it))!!}
//    }
//
//    fun renderLogMessages(messages: List<RedisLogMessage>): ToReactElementable {
//        var ultimateMessages = messages
//        if (urlQuery.skipCrap) {
//            ultimateMessages = ultimateMessages.filterNot(this::isCrap)
//        }
//
//        if (ultimateMessages.isEmpty()) return kdiv {o->
//            o- "No shit here..."
//        }
//
//        return kdiv {o->
//            o+ ultimateMessages.map {msg->
//                fun renderMsgText() = kdiv(whiteSpace = "pre", fontFamily = "monospace"){o->
//                    o- msg.text
//                }
//
//                kdiv(position = "relative", className = CN_ROW){o->
//                    exhaustive=when (msg) {
//                        is Separator -> {
//                            o- when (msg.type) {
//                                SEPARATOR -> renderSeparator(msg, "1px solid $BLUE_500", "0.75em")
//                                THICK_SEPARATOR -> renderSeparator(msg, "5px solid $BLUE_500", "0.55em")
//                                THICK_DASHED_SEPARATOR -> renderSeparator(msg, "5px dashed $BLUE_500", "0.55em")
//                            }
//                        }
//
//                        is Fuck -> {
//                            var virgin = true
//
//                            val content = Placeholder(kdiv(fontStyle = "italic"){o->
//                                o- "Loading..."
//                            })
//
//                            o- Betsy(msg.text,
//                                     content,
//                                     headerClassName = CN_HOVER_HIGHLIGHT,
//                                     onExpand = {
//                                         if (virgin) {
//                                             virgin = false
//                                             async<Unit> {
//                                                 val children = await(getLogMessages(msg.id))
//                                                 content.setContent(renderLogMessages(children))
//                                                 content.update()
//                                             }
//                                         }
//                                     },
//                                     renderInTitle = {renderInTitle(it, msg)},
//                                     renderInHeader = {renderStamp(it, msg)})
//                        }
//
//                        is SQL -> {
//                            val short = msg.shortDescription
//                            if (short != null) {
//                                o- Betsy(
//                                    "SQL: $short",
//                                    kdiv{o->
//                                        o- renderMsgText()
//                                        msg.result?.let {
//                                            o- line()
//                                            for ((i, rec) in it.withIndex()) {
//                                                o- Betsy(
//                                                    "Record $i",
//                                                    kul{o->
//                                                        for (fv in rec.fieldValues) {
//                                                            o- kli{o->
//                                                                o- "${fv.fieldName} = ${fv.value}"
//                                                            }
//                                                        }
//                                                    })
//                                            }
//                                        }
//                                    },
//                                    className = CN_HOVER_HIGHLIGHT,
//                                    renderInTitle = {renderInTitle(it, msg)},
//                                    renderInHeader = {renderStamp(it, msg)})
//                            }
//                            else
//                                o- renderMsgText()
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun isCrap(msg: RedisLogMessage): Boolean {
//        if (msg.text.contains("/rpc/testTakeSnapshot")) return false
//        return listOf(
//            "/rpc/ping", "/rpc/jsonProcedure", "/rpc/fuckingRemoteProcedure", "/rpc/impose", "/rpc/test"
//        ).any {
//            msg.text.contains(it)
//        }
//    }
//
//    fun renderSeparator(msg: RedisLogMessage, borderStyle: String, offset: String) =
//        kdiv(className = CN_HOVER_HIGHLIGHT){o->
//            o- kdiv(borderBottom = borderStyle, position = "absolute", top = offset, width = "100%")
//            o- kdiv(position = "relative", left = "1em"){o->
//                o- kspan(className = CN_OPAQUE, paddingLeft = "0.5em", paddingRight = "0.5em"){o->
//                    o- msg.text
//                    renderInTitle(o, msg)
//                }
//            }
//            renderStamp(o, msg)
//        }
//
//    fun renderInTitle(o: ElementBuilder, msg: RedisLogMessage) {
//        msg.endMillis?.let {
//            o- "${nbsp+nbsp+nbsp}${it - msg.beginMillis}ms"
//        }
//    }
//
//    fun renderStamp(o: ElementBuilder, msg: RedisLogMessage) {
//        o- kdiv(className = CN_OPAQUE, position = "absolute", top = 0, right = 0, paddingLeft = "0.5em"){o->
//            var title = msg.stamp
//            title += when {
//                !title.contains(".") -> ".000"
//                Regex("\\.\\d\$").find(title) != null -> "00"
//                Regex("\\.\\d\\d\$").find(title) != null -> "0"
//                else -> ""
//            }
//
//            o- renderRawStackLink(msg.stack, title)
//        }
//    }
}

//private fun line() = reactCreateElement(
//    "hr",
//    json(
//        "style" to json(
//            "marginTop" to "1rem",
//            "marginBottom" to "1rem",
//            "borderTop" to "1px solid $GRAY_500"
//        )
//    )
//)














