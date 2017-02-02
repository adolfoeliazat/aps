package aps.front

import aps.*
import into.kommon.*

class ConfirmOrderPage(val world: World) {
    val urlQuery = URLQuery()
    inner class URLQuery : URLQueryBase(world) {
        val secret by stringURLParam()
    }

    suspend fun load() {
        val res = send(ConfirmOrderRequest() - {o ->
            o.secret.value = urlQuery.secret
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
                imf("aaaaaaaaaaaaa")
            }
        }
    }
}


