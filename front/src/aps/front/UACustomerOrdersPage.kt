package aps.front

import aps.*
import into.kommon.*

class UACustomerOrdersPage(val world: World) {
    suspend fun load() {
        val m = Melinda<UAOrderRTO, Nothing, CustomerOrderFilter>(
            ui = world,
            urlPath = "orders.html",
            procedureName = "customerGetOrders",
            header = {t("My Orders", "Мои заказы")},
            filterSelectValues = CustomerOrderFilter.values(),
            defaultFilter = CustomerOrderFilter.ALL,

            renderItem = {index, order ->
                kdiv {o ->
                    o - "pizda"
                }
            }
        )

        m.specifyPlus(
            plusFormSpec = FormSpec<UACustomerCreateOrderRequest, UACustomerCreateOrderRequest.Response>(
                UACustomerCreateOrderRequest(world.xlobal), world,
                primaryButtonTitle = t("Create", "Создать"),
                cancelButtonTitle = const.defaultCancelButtonTitle),
            onPlusFormSuccessa = {res->
                world.pushNavigate("order.html?id=${res.id}")
            }
        )

        m.ignita()
    }
}


