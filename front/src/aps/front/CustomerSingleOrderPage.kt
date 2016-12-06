package aps.front

import aps.*


class CustomerSingleOrderPage(val world: World) {
    fun load(): Promise<Unit> = async {
        world.setPage(Page(
            header = pageHeader0(t("TOTE", "Мои заказы")),
            body = kdiv{o->
                o- "fuck you"
            }
        ))
    }
}


