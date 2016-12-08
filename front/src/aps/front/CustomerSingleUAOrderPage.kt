package aps.front

import aps.*
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
            header = pageHeader0(t("TOTE", "Заказ #${order.id}")),
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
                            o- kdiv(){o->
                                o- formatUnixTime(order.insertedAt)
                            }
//                            creator_id bigint not null references users(id), -- Can be admin
//                            customer_id bigint not null references users(id),
//                            title text not null,
//                            document_type ua_document_type not null,
//                            deadline timestamp /*maybe null*/,
//                            price int /*maybe null*/,
//                            num_pages int not null,
//                            num_sources int not null,
//                            details text not null,
//                            admin_notes text not null,
//                            state order_state not null,
//                            writer_id bigint /*maybe null*/ references users(id)
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


