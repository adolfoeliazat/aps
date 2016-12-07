package aps.front

import aps.*

class CustomerSingleUAOrderPage(val world: World) {
    class URLQuery {
        var id: String? = null
    }

    fun load(): Promise<Unit> = async {
        val urlQuery = typeSafeURLQuery(world){URLQuery()}
        val orderID = urlQuery.id.nullifyBlank() ?: return@async world.setShittyParamsPage()

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
                o- order.title
            }
        ))
    }
}


