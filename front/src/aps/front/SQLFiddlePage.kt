package aps.front

import aps.*
import aps.Color.*
import kotlin.coroutines.CoroutineContext

class SQLFiddlePage(val world: World) {
    val input: Input by mere(Input(
        kind = Input.Kind.TEXTAREA,
        autoFocus = true,
        initialValue = "select * from ua_orders",
        onKeyDowna = {
            if (it.ctrlKey && it.key == "Enter") {
                spew.setContent(span("Working like a dog..."))
                val res = send(TestSQLFiddleRequest()-{o->
                    o.input.value = input.value
                })
                spew.setContent(kdiv(whiteSpace = "pre",
                                     fontFamily = "monospace",
                                     color = ifOrNull(res.isError){Color.RED_700}){o->
                    o- res.spew
                })
            }
        }))

    val spew = Placeholder()

    suspend fun load() {
        world.setPage(Page(
            header = usualHeader("SQL Fiddle"),
            body = kdiv{o->
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














