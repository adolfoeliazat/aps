package aps.front

import aps.*
import aps.Color.*

class DebugMailboxPage(val world: World) {
    inner class URLQuery : URLQueryBase_killme(world) {
    }

    val urlQuery = URLQuery()

    suspend fun load() {
        val res = await(send(GetSentEmailsRequest()))
        world.setPage(Page(
            header = pageHeader2("Mailbox"),

//            headerControls = BurgerDropdownButton(Menu(listOf(
//                MenuItem("Clear") {async{
//                    clog("Clearing all fucking shit")
//                    await(fedis.del(listOf("${RedisLogMessage.ROOT_ID}:children")))
//                    Globus.realLocation.reload()
//                }}
//            ))),

            body = kdiv{o->
                val c = css.test.mailbox
                fun String.div(block: (ElementBuilder) -> Unit) = kdiv(this, block)

                o- c.messages.div {o->
                    for (email in res.emails) {
                        o- c.message.div {o->
                            fun section(title: String, value: String) = c.section.div {o->
                                o- c.sectionTitle.div {it-title}
                                o- c.sectionValue.div {it-value}
                            }

                            o- c.header.div {o->
                                o- section("To:", email.to)
                                o- section("Subject:", email.subject)
                            }
                            o- c.body.div {o->
                                o- rawHTML(email.html)
                            }
                        }
                    }
                }
            }
        ))
    }

}














