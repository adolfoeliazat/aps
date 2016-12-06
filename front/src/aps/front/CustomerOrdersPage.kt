package aps.front

import aps.*
import into.kommon.*

class CustomerOrdersPage(val world: World) {
    fun load(): Promise<Unit> {
        return Melinda<OrderRTO, Nothing, CustomerOrderFilter>(
            ui = world,
            urlPath = "orders.html",
            procedureName = "customerGetOrders",
            header = {pageHeader0(t("TOTE", "Заказы"))},
            filterSelectValues = CustomerOrderFilter.values(),
            defaultFilter = CustomerOrderFilter.ALL,
            plusFormSpec = FormSpec<CustomerCreateUAOrderRequest, GenericResponse>(
                CustomerCreateUAOrderRequest(), world,
                primaryButtonTitle = t("TOTE", "Создать"),
                cancelButtonTitle = defaultCancelButtonTitle),

            renderItem = {index, order -> kdiv {o->

            }}
        ).ignita()
    }
}


