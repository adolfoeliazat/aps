package aps.front

import aps.*
import into.kommon.*

class MakeOrderPage(val world: World) {
    val place: Placeholder by mere(Placeholder(
        kdiv(marginBottom = "1em"){o->
                o- FormMatumba<UACustomerCreateOrderRequest, UACustomerCreateOrderRequest.Response>(FormSpec(
                    req = UACustomerCreateOrderRequest(world.xlobal), ui = world,
                    primaryButtonTitle = t("Proceed", "Продолжить"),
                    onSuccessa = {
                        if (world.userMaybe != null) {
                            imf("MakeOrderPage for signed-in user")
                        } else {
                            place.setContent(kdiv{o->
                                o- t("TOTE", "Мы отправили письмо. Нажми там на ссылку для подтверждения заказа.")
                            })
                        }
                    }
                ))
            }
    ))

    suspend fun load() {
        world.setPage(Page(
            header = usualHeader(t("TOTE", "Заказ")),
            body = place
        ))
    }
}

