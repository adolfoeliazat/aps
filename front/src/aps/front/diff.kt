package aps.front

import aps.*
import into.kommon.*
import aps.Color.*
import kotlin.browser.window
import kotlin.js.json
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

external interface JsDiffItem {
    val added: Boolean
    val removed: Boolean
    val value: String
}

class RenderDiffResult(val tre: ToReactElementable, val diffSummary: String)

fun renderDiff(
    expected: String, actual: String,
    actualTestShit: String,
    actualPaste: String? = null,
    expectedTitle: String = "Expected",
    actualTitle: String = "Actual"
): RenderDiffResult {
    val diffSummary = StringBuilder()

    val tabSpecs = mutableListOf<TabSpec>()
    tabSpecs.add(SimpleTabSpec(
        key = tabs.shebang.diff,
        title = "Diff",
        renderBody = {kdiv(whiteSpace = "pre"){o->
            val placeholders = mutableListOf<Placeholder>()
            var prevLabel: String? = null
            val JsDiff = global.JsDiff
            val diffLineItems = jsArrayToList<JsDiffItem>(JsDiff.diffLines(expected, actual))
            for ((i, item) in diffLineItems.withIndex()) {
                var titleClass by notNull<String>()
                val contentClass: String
                val label: String?

                if (item.added) {
                    diffSummary += "Added: ${item.value}\n\n"
                    titleClass = css.diff.actual.title
                    contentClass = css.diff.actual.content
                    label = actualTitle
                } else if (item.removed) {
                    diffSummary += "Removed: ${item.value}\n\n"
                    titleClass = css.diff.expected.title
                    contentClass = css.diff.expected.content
                    label = expectedTitle
                } else {
                    contentClass = css.diff.same.content
                    label = null
                }
                if (label != null && label != prevLabel) {
                    o- kdiv(className = titleClass){o->
                        o- label
                    }
                }
                prevLabel = label

                o- kdiv(className = contentClass){o->
                    placeholders += Placeholder(span(item.value))
                    o- placeholders.last()
                }
            }
        }}))

    if (actualPaste != null) {
        tabSpecs.add(SimpleTabSpec(
            key = tabs.shebang.actualPaste,
            title = "Actual Paste",
            renderBody = {kdiv{o->
                o- Input(json("initialValue" to actualPaste,
                              "kind" to "textarea",
                              "rows" to 10,
                              "style" to json("width" to "100%",
                                              "height" to "100%"),
                              "untested" to true))}}))
    }

    val tabs = Tabs2(initialActiveKey = tabs.shebang.diff, tabs = tabSpecs)


    return RenderDiffResult(
        tre = kdiv(position = "relative"){o->
            o- tabs

            TestGlobal.testShitBeingAssertedID?.let {id->
                o- kdiv(position = "absolute", right = 0, top = 0){o->
                    val holder = Placeholder()
                    holder.setContent(button2(span("Update Test Shit"), level = "primary") {async<Unit>{
                        holder.setContent(span("Working like a dog..."))
                        try {
                            await(fuckingRemoteCall.updateTestShit(id, actualTestShit))
                            Globus.realLocation.href = TestGlobal.lastTestHref
                        } catch(e: Throwable) {
                            holder.setContent(kspan(color = RED_900){it-"No fucking way"})
                            throw e
                        }
                    }})
                    o- holder
                }
            }
        },

        diffSummary = diffSummary.toString()
    )
}


