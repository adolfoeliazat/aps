package aps.front

import aps.*

class MakeOrderPage(val world: World) {
    suspend fun load() {
        world.setPage(Page(
            header = usualHeader(t("TOTE", "Заказ")),
            body = kdiv{o->
                o- "Fuck you"
            }
        ))
    }
}

