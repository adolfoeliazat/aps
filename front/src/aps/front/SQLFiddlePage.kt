package aps.front

import aps.*
import aps.Color.*
import kotlin.coroutines.CoroutineContext

class SQLFiddlePage(val world: World) {
    val input: Input by mere(Input(
        kind = Input.Kind.TEXTAREA,
        onKeyDown = {
            if (it.ctrlKey && it.key == "Enter") {
                val sql = input.value
                spew.setContent(kdiv(whiteSpace = "pre", fontFamily = "monospace"){o->
                    o- "shit:\n$sql"
                })
            }
        }))

    val spew = Placeholder()

    suspend fun load() {
        world.setPage(Page(
            header = usualHeader("SQL Fiddle"),
            body = kdiv{o->
//                val c = css.test.sqlfiddle
//                fun String.div(block: (ElementBuilder) -> Unit) = kdiv(this, block)
                o- kdiv{o->
                    o- label("Enter your shit:")
                    o- input
                }
                o- kdiv(marginTop = "1em"){o->
                    o- spew
                }
            }
        ))
    }
}














