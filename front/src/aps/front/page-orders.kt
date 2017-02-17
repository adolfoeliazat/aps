package aps.front

import aps.*
import into.kommon.*

class UAAdminOrdersPage(val world: World) {
    object urlQuery : URLQueryParamsMarker {
        val filter by EnumURLParam(AdminOrderFilter.values(), default = AdminOrderFilter.ALL)
    }

    suspend fun load(): PageLoadingError? {
        val res = sendUAAdminGetOrders(ItemsRequest(AdminOrderFilter.values())-{o->
            o.entityID.value = 10L
            o.filter.value = urlQuery.filter.get()
            o.ordering.value = Ordering.ASC
            o.searchString.value = ""
            o.fromID.value = 0L
        })
        return when (res) {
            is FormResponse2.Shitty -> {
                PageLoadingError(res.error)
            }
            is FormResponse2.Hunky -> {
                setPage(kdiv{o->
                    o- "fuck you"
                })
                pageLoadedFineResult
            }
        }
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
    suspend fun load(): PageLoadingError? {
        val m = Pizdalinda<UAOrderRTO, Nothing, CustomerOrderFilter>(
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
        return null
    }
}


