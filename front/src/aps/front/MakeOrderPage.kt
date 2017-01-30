package aps.front

import aps.*

class MakeOrderPage(val world: World) {
    suspend fun load() {
        world.setPage(Page(
            header = usualHeader(t("TOTE", "Заказ")),
            body = kdiv(marginBottom = "1em"){o->
                o- FormMatumba<UACustomerCreateOrderRequest, UACustomerCreateOrderRequest.Response>(FormSpec(
                    UACustomerCreateOrderRequest(world.xlobal), world
                ))
            }
        ))
    }
}

