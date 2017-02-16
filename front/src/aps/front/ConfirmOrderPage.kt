package aps.front

import aps.*
import into.kommon.*

class ConfirmOrderPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val secret by MaybeStringURLParam()
    }

    suspend fun load() {
        val res = send(ConfirmOrderRequest()-{o->
            o.secret.value = urlQuery.secret.get(world)
        })
        exhaustive/when (res) {
            is FormResponse2.Shitty -> {
                world.setPage(Page(
                    header = usualHeader(t("TOTE", "Подтверждение заказа")),
                    body = kdiv{o->
                        o- res.error
                    }
                ))
            }

            is FormResponse2.Hunky -> {
                val u = res.meat.userSignedInAsPartOfMakingOrder
                if (u != null) {
                    world.userMaybe = u.user
                    world.tokenMaybe = u.token
                    Globus.currentBrowseroid.typedStorageLocal.token = u.token
                    world.updateNavbar()
                }

                world.replaceNavigate(
                    // TODO:vgrechka Use makeUrl
                    pages.uaCustomer.order.path + ".html"
                        + "?${UACustomerSingleOrderPage.urlQuery.id.name}=${res.meat.orderId}")
            }
        }
    }
}


