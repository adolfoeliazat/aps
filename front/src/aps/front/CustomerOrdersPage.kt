package aps.front

import aps.*
import into.kommon.*

class CustomerOrdersPage(val world: World) {
    fun load(): Promise<Unit> {
        val m = Melinda<OrderRTO, Nothing, CustomerOrderFilter>(
            ui = world,
            urlPath = "orders.html",
            procedureName = "customerGetOrders",
            header = {pageHeader0(t("TOTE", "Мои заказы"))},
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
                primaryButtonTitle = t("TOTE", "Создать"),
                cancelButtonTitle = defaultCancelButtonTitle),
            onPlusFormSuccessa = {
                //res: CustomerCreateUAOrderRequest.Response ->
//                world.pushNavigate("order?id=${res.id}")
                Promise.resolve(Unit)
            }
        )

        return m.ignita()
    }
}


