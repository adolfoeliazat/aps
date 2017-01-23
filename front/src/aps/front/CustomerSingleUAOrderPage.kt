@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import aps.front.frontSymbols.numberSign
import into.kommon.*
import kotlin.properties.Delegates.notNull

interface CustomerSingleUAOrderPageTab {
    val tabSpec: TabSpec
    fun load(): Promisoid<ZimbabweResponse.Shitty<*>?>
}


class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
        var tab: String? = null
    }

    var orderID by notNull<String>()

    fun load(): Promisoid<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()
        val tabID = urlQuery.tab ?: "params"

        val res = await(send(world.token, LoadUAOrderRequest()-{o->
            o.id.value = orderID
        }))
        val order = when (res) {
            is ZimbabweResponse.Shitty -> return@async world.setShittyResponsePage(res)
            is ZimbabweResponse.Hunky -> res.meat.order
        }

        val tabs = listOf(
            ParamsTab(world, order),
            CustomerSingleUAOrderPageFilesTab(this, world, order),
            MessagesTab(order)
        )
        val tab = tabs.find {it.tabSpec.id == tabID} ?: tabs.first()

        val error = await(tab.load())
        error?.let {return@async world.setShittyResponsePage(it)}

        world.setPage(Page(
            header = pageHeader3(kdiv{o->
                o- t("TOTE", "Заказ $numberSign${order.id}")
                o- kspan(backgroundColor = order.state.labelBackground,
                         fontSize = "60%",
                         padding = "0.1em 0.3em",
                         borderRadius = "0.3em",
                         marginLeft = "1em",
                         position = "relative",
                         top = "-0.2em"){o->
                    o- order.state.title.replace(" ", symbols.nbsp)
                }
            }),

            body = kdiv{o->
                o- h4(marginBottom = "0.7em"){o->
                    o- order.title
                }

                o- Tabs2(
                    initialActiveID = tab.tabSpec.id,
                    switchOnTabClick = false,
                    tabDomIdPrefix = "tab-",
                    onTabClicka = this::clickOnTab,
                    tabs = tabs.map {it.tabSpec}
                )
            }
        ))
    }

    fun clickOnTab(id: String): Promisoid<Unit> = async {
        await(effects).blinkOn(byid("tab-$id"), BlinkOpts(widthCalcSuffix = "- 0.15em"))
        try {
            await(world.pushNavigate("order.html?id=$orderID&tab=$id"))
        } finally {
            await(effects).blinkOffFadingOut()
        }
    }

}

private class ParamsTab(val world: World, val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    override fun load(): Promisoid<ZimbabweResponse.Shitty<*>?> = async {
        null
    }

    private val content = kdiv{o->
        fun label(title: String) = klabel(marginBottom = 0) {it-title}

        fun row(build: (ElementBuilder) -> Unit) =
            kdiv(className = "row", marginBottom = "0.5em"){o->
                build(o)
            }

        exhaustive/when (world.user.kind) {
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
    }

    override val tabSpec = TabSpec("params", t("TOTE", "Параметры"), content)
}


private class MessagesTab(val order: UAOrderRTO) : CustomerSingleUAOrderPageTab {
    override fun load(): Promisoid<ZimbabweResponse.Shitty<*>?> = async {
        throw UnsupportedOperationException("Implement me, please, fuck you")
    }

    private val content = kdiv{o->
        o- "fucking messages"
    }

    override val tabSpec = TabSpec("messages", t("TOTE", "Сообщения"), content)
}















