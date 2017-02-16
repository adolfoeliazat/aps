package aps.front

import aps.*
import into.kommon.*

class UAAdminOrdersPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val filter by EnumURLParam(AdminOrderFilter.values(), default = AdminOrderFilter.ALL)
    }

    suspend fun load() {
        setPage(kdiv{o->
            o- "fuck you"
        })
    }

    private fun setPage(body: ToReactElementable) {
        world.setPage(Page(
            header = usualHeader(t("Orders", "Заказы")),
            body = kdiv{o->
                o- body
            }
        ))
    }
}

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
                req = UACustomerCreateOrderRequest(world.xlobal),
                ui = world,
                primaryButtonTitle = t("Create", "Создать"),
                cancelButtonTitle = const.text.shebang.defaultCancelButtonTitle),
            onPlusFormSuccessa = {res->
                world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
                    URLParamValue(UACustomerSingleOrderPage.urlQuery.id, res.id)
                )))
            }
        )

        m.ignita()
    }
}


