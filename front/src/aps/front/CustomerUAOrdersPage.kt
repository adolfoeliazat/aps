package aps.front

import aps.*
import into.kommon.*

class CustomerUAOrdersPage(val world: World) {
    fun load(): Promisoid<Unit> {
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
            plusFormSpec = FormSpec<CustomerCreateUAOrderRequest, CustomerCreateUAOrderRequest.Response>(
                CustomerCreateUAOrderRequest(), world,
                primaryButtonTitle = t("Create", "Создать"),
                cancelButtonTitle = const.defaultCancelButtonTitle),
            onPlusFormSuccessa = {res->
                world.pushNavigate("order.html?id=${res.id}")
            }
        )

        return m.ignita()
    }
}


