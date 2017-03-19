package aps.front

import aps.*

class MakeOrderPage(val world: World) {
    val place: Placeholder by mere(Placeholder(
        kdiv(marginBottom = "1em"){o->
            o- FormMatumba<UAOrderParamsRequest, Any>(FormSpec(
                procedureName = "UACreateOrder",
                req = UAOrderParamsRequest(isAdmin = false, isUpdate = false),
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

    suspend fun load(): PageLoadingError? {
        world.setPage(Page(
            header = usualHeader(t("TOTE", "Заказ")),
            body = place
        ))
        return null
    }
}

