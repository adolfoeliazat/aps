package aps.front

import aps.*
import into.kommon.*

class DebugWordMentionFinderPage(val world: World) {
    private val typedStorageLocal = Globus.realTypedStorageLocal

    val input = Input(kind = Input.Kind.TEXTAREA, rows = 15,
                      initialValue = typedStorageLocal.DebugWordMentionFinderPage_inputText ?: "")

    val outPlaceholder = Placeholder()

    suspend fun load() {
        world.setPage(Page(
            header = pageHeader2("Word Mention Finder"),
            body = kdiv{o->
                o- input
                o- Button(title = "Analyze Shit", level = Button.Level.PRIMARY, style = Style(marginTop = "1em"), onClicka = {async{
                    val wholeText = input.getValue()
                    typedStorageLocal.DebugWordMentionFinderPage_inputText = wholeText

                    aps.gloshit.wholeText = wholeText
                    // tokens.forEach {console.log(it.toString())}
                    val docs = splitTextToDocuments(wholeText)
                    val allStems = mutableListOf<String>()
                    val stemsByDoc = mutableListOf<Set<String>>()
                    for (doc in docs) {
                        val tokens = await(fuckingRemoteCall.luceneParseRussian(doc))
                        val stems = tokens.fold(setOf<String>()) {r, x -> r + x.text}
                        allStems += stems
                        stemsByDoc += stems
                    }

                    val stemToDocIndexes = mutableMapOf<String, List<Int>>()
                    for (stem in allStems) {
                        val docIndexes = mutableListOf<Int>()
                        for ((i, stems) in stemsByDoc.withIndex()) {
                            if (stems.contains(stem)) {
                                docIndexes += i
                            }
                        }
                        stemToDocIndexes[stem] = docIndexes
                    }

                    val out = StringBuilder()
                    for ((stem, docIndexes) in stemToDocIndexes) {
                        if (docIndexes.size > 1) {
                            out.append(stem + ": " + docIndexes.map{docs[it].trim().lines()[0]}.joinToString(", ") + "\n")
                        }
                    }
                    outPlaceholder.setContent(kdiv(fontFamily = "monospace", whiteSpace = "pre", marginTop = "1em"){o->
                        o- out.toString()
                    })
                }})

                o- outPlaceholder
            }
        ))
    }

    private fun splitTextToDocuments(wholeText: String): List<String> {
        val docs = wholeText.split(Regex("----*"))
        return docs
    }
}












