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
                            fun label(title: String) = klabel(marginBottom = 0) {it-title}

                            fun row(build: (ElementBuilder) -> Unit) =
                                kdiv(className = "row", marginBottom = "0.5em"){o->
                                    build(o)
                                }

                            exhaustive/when (world.userSure.kind) {
                                UserKind.CUSTOMER -> {
                                    o- row{o->
                                        o- kdiv(className = "col-md-4"){o->
                                            o- label(t("TOTE", "Создан"))
                                            o- kdiv(){o->
                                                o- formatUnixTime(order.insertedAt)
                                            }
                                        }
                                        o- kdiv(className = "col-md-4"){o->
                                            o- label(t("TOTE", "Срок"))
                                            o- kdiv(){o->
                                                o- formatUnixTime(order.deadline)
                                            }
                                        }
                                    }
                                    o- row{o->
                                        o- kdiv(className = "col-md-4"){o->
                                            o- label(t("TOTE", "Тип документа"))
                                            o- kdiv(){o->
                                                o- order.documentType.title
                                            }
                                        }
                                        o- kdiv(className = "col-md-4"){o->
                                            o- label(t("TOTE", "Страниц"))
                                            o- kdiv(){o->
                                                o- order.numPages.toString()
                                            }
                                        }
                                        o- kdiv(className = "col-md-4"){o->
                                            o- label(t("TOTE", "Источников"))
                                            o- kdiv(){o->
                                                o- order.numSource.toString()
                                            }
                                        }
                                    }
                                    order.price?.let {
                                        o- kdiv(){o->
                                            o- formatUAH(it)
                                        }
                                    }
                                    o- row{o->
                                        o- kdiv(className = "col-md-12"){o->
                                            o- label(t("TOTE", "Детали"))
                                            o- kdiv(whiteSpace = "pre-wrap"){o->
                                                o- order.details
                                            }
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


