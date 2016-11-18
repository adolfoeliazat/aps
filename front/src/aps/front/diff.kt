package aps.front

import aps.*
import into.kommon.*
import aps.front.Color.*

@native interface JsDiffItem {
    val added: Boolean
    val removed: Boolean
    val value: String
}

fun renderDiff(greenString: String, redString: String, greenTitle: String = "Expected", redTitle: String = "Actual"): ToReactElementable {
    val noisy = false

    val divs = mutableListOf<ToReactElementable>()
    val placeholders = mutableListOf<Placeholder>()
    var prevLabel: String? = null
    val JsDiff = global.JsDiff
    val diffLineItems = jsArrayToList<JsDiffItem>(JsDiff.diffLines(greenString, redString))
    for ((i, item) in diffLineItems.withIndex()) {
        val backgroundColor: Color
        val label: String?
        if (item.added) {
            backgroundColor = RED_100
            label = redTitle
        } else if (item.removed) {
            backgroundColor = GREEN_100
            label = greenTitle
        } else {
            backgroundColor = WHITE
            label = null
        }
        if (label != null && label != prevLabel) {
            divs += kdiv(backgroundColor=backgroundColor, fontWeight="bold"){o->
                o- label
            }
        }
        prevLabel = label

        divs += kdiv(backgroundColor=backgroundColor){o->
            placeholders += Placeholder(span(item.value))
            o- placeholders.last()
        }

        if (i > 0) {
            val prevItem = diffLineItems[i - 1]
            if (item.added && prevItem.removed) {
                val diffCharItems = jsArrayToList<JsDiffItem>(JsDiff.diffChars(item.value, prevItem.value))

                if (noisy) {
                    clog("--------------")
                    for (dci in diffCharItems) {
                        clog("DCI", dci)
                    }
                }

                for (idx in i-1..i) {
                    val shit = diffLineItems[idx]
                    placeholders[idx].setContent(kdiv{o->
                        for (dci in diffCharItems) {
                            if (dci.added && shit.removed || dci.removed && shit.added)
                                o- kspan(border = "1px solid ${if (idx == i - 1) GREEN_900 else RED_900}",
                                         backgroundColor = if (idx == i - 1) GREEN_200 else RED_200,
                                         padding = 2, margin = 2){o->
                                    o- dci.value}
                            else if (!dci.added && !dci.removed)
                                o- dci.value
                        }
                    })
                }
            }
        }
    }

    return kdiv(whiteSpace="pre"){o->
        o+ divs
    }
}

