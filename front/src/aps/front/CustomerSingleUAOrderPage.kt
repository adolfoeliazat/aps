package aps.front

import aps.*
import into.kommon.*
import jquery.jq

class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
        var tab: String? = null
    }

    fun load(): Promise<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        val orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()
        val tab = urlQuery.tab ?: "params"

        val res = await(send(world.tokenSure, LoadUAOrderRequest()-{o->
            o.id.value = orderID
        }))
        val order = when (res) {
            is ZimbabweResponse.Shitty -> return@async world.setShittyResponsePage(res)
            is ZimbabweResponse.Hunky -> res.meat.order
        }

        world.setPage(Page(
            header = pageHeader3(kdiv{o->
                o- t("TOTE", "Заказ #${order.id}")
//                o- " "
                o- kspan(backgroundColor = order.state.labelBackground,
                         fontSize = "60%",
                         padding = "0.1em 0.3em",
                         borderRadius = "0.3em",
                         marginLeft = "1em",
                         position = "relative",
                         top = "-0.2em"){o->
                    o- order.state.title.replace(" ", nbsp)
                }
            }),

            body = kdiv{o->
                o- h4(marginBottom = "0.7em"){o->
                    o- order.title
                }

                o- Tabs2(
                    initialActiveID = tab,
                    switchOnTabClick = false,
                    tabDomIdPrefix = "tab-",

                    onTabClicka = {id-> async {
                        effects2.blinkOn(byid("tab-$id"), widthCalcSuffix = "- 0.15em")
                        await(world.pushNavigate("order.html?id=$orderID&tab=$id"))
                        effects2.blinkOffFadingOut()
                    }},

                    tabs = listOf(
                        TabSpec("params", t("TOTE", "Параметры"), kdiv{o->
                            exhaustive/when (world.userSure.kind) {
                                UserKind.CUSTOMER -> {
                                    o- kdiv(){o->
                                        o- formatUnixTime(order.insertedAt)
                                    }
                                    o- kdiv(){o->
                                        o- order.documentType.title
                                    }
                                    o- kdiv(){o->
                                        o- formatUnixTime(order.deadline)
                                    }
                                    order.price?.let {
                                        o- kdiv(){o->
                                            o- formatUAH(it)
                                        }
                                    }
                                    o- kdiv(){o->
                                        o- order.numPages.toString()
                                    }
                                    o- kdiv(){o->
                                        o- order.numSource.toString()
                                    }
                                    o- kdiv(){o->
                                        o- kdiv(whiteSpace = "pre-wrap"){o->
                                            o- order.details
                                        }
                                    }
                                }

                                UserKind.WRITER -> imf()

                                UserKind.ADMIN -> imf()
                            }
                        }),

                        TabSpec("files", t("TOTE", "Файлы"), kdiv{o->
                            o- "fucking files"
                        }),

                        TabSpec("messages", t("TOTE", "Сообщения"), kdiv{o->
                            o- "fucking messages"
                        })
                    )
                )
            }
        ))
    }
}


