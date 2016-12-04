package aps.front

import aps.*
import into.kommon.*
import aps.front.Color.*
import kotlin.browser.window

@native interface JsDiffItem {
    val added: Boolean
    val removed: Boolean
    val value: String
}

fun renderDiff(
    expected: String, actual: String,
    actualTestShit: String,
    actualPaste: String = actual,
    expectedTitle: String = "Expected",
    actualTitle: String = "Actual"
): ToReactElementable {
    val noisy = false

    val tabs = Tabs2(initialActiveID = "diff", tabs = listOf(
        TabSpec(id = "diff",
                title = "Diff",
                content = kdiv(whiteSpace="pre"){o->
                    val placeholders = mutableListOf<Placeholder>()
                    var prevLabel: String? = null
                    val JsDiff = global.JsDiff
                    val diffLineItems = jsArrayToList<JsDiffItem>(JsDiff.diffLines(expected, actual))
                    for ((i, item) in diffLineItems.withIndex()) {
                        val backgroundColor: Color
                        val label: String?
                        if (item.added) {
                            backgroundColor = RED_100
                            label = actualTitle
                        } else if (item.removed) {
                            backgroundColor = GREEN_100
                            label = expectedTitle
                        } else {
                            backgroundColor = WHITE
                            label = null
                        }
                        if (label != null && label != prevLabel) {
                            o- kdiv(backgroundColor=backgroundColor, fontWeight="bold"){o->
                                o- label
                            }
                        }
                        prevLabel = label

                        o- kdiv(backgroundColor=backgroundColor){o->
                            placeholders += Placeholder(span(item.value))
                            o- placeholders.last()
                        }

//                        if (i > 0) {
//                            val prevItem = diffLineItems[i - 1]
//                            if (item.added && prevItem.removed) {
//                                val diffCharItems = jsArrayToList<JsDiffItem>(JsDiff.diffChars(item.value, prevItem.value))
//
//                                if (noisy) {
//                                    clog("--------------")
//                                    for (dci in diffCharItems) {
//                                        clog("DCI", dci)
//                                    }
//                                }
//
//                                for (idx in i-1..i) {
//                                    val shit = diffLineItems[idx]
//                                    placeholders[idx].setContent(kdiv{o->
//                                        for (dci in diffCharItems) {
//                                            if (dci.added && shit.removed || dci.removed && shit.added)
//                                                o- kspan(border = "1px solid ${if (idx == i - 1) GREEN_900 else RED_900}",
//                                                         backgroundColor = if (idx == i - 1) GREEN_200 else RED_200,
//                                                         padding = 2, margin = 2){o->
//                                                    o- dci.value}
//                                            else if (!dci.added && !dci.removed)
//                                                o- dci.value
//                                        }
//                                    })
//                                }
//                            }
//                        }
                    }
                }),

        TabSpec(id = "actualPaste",
                title = "Actual Paste",
                content = kdiv{o->
                    o- Input(json("initialValue" to actualPaste,
                                  "kind" to "textarea",
                                  "rows" to 10,
                                  "style" to json("width" to "100%",
                                                  "height" to "100%"),
                                  "untested" to true))
                })
    ))

//    val tabs = Tabs(json(
//        "activeTab" to "diff",
//        "tabs" to json(
//            "diff" to json(
//                "title" to "Diff",
//                "content" to "qweqweqwe"
////                "content" to kdiv(whiteSpace="pre"){o->
////                    o+ divs
////                }.toReactElement()
//            )
////            "actual" to json(
////                "title" to "Actual",
////                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")), actualString)),
////            "expected" to json(
////                "title" to "Expected",
////                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")), expectedString)),
////            "actualPaste" to json(
////                "title" to "Actual Paste",
////                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")),
////                                         Shitus.Input(json("initialValue" to redString, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true))))
////            "actualPasteWithExt" to json(
////                "title" to "Actual Paste + ext",
////                "content" to Shitus.diva(json("style" to json("whiteSpace" to "pre-wrap")),
////                                         Shitus.Input(json("initialValue" to actualStringForPastingPlusExt, "kind" to "textarea", "rows" to 10, "style" to json("width" to "100%", "height" to "100%"), "untested" to true))))
//        )
//    ))

    return kdiv(position = "relative"){o->
        o- tabs

        TestGlobal.testShitBeingAssertedID?.let {id->
            o- kdiv(position = "absolute", right = 0, top = 0){o->
                val holder = Placeholder()
                holder.setContent(button2(span("Update Test Shit"), level = "primary") {async<Unit>{
                    holder.setContent(span("Working like a dog..."))
                    try {
                        await(fuckingRemoteCall.updateTestShit(id, actualTestShit))
                        window.location.href = TestShit.lastTestHref!!
                    } catch(e: Throwable) {
                        holder.setContent(kspan(color = RED_900){it-"No fucking way"})
                        throw e
                    }
                }})
                o- holder
            }
        }
    }
}

//o- object {
//    val holder = Placeholder()
//
//    init {
//        button2(span("update test shit"), level = "primary") {
//            clog("aaaaaaaaaa")
//        }
//    }
//}.holder
//

