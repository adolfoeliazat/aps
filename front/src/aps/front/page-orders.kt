package aps.front

import aps.*

class UAAdminOrdersPage {
    object urlQuery : URLQueryParamsMarker {
        val filter by EnumURLParam(AdminOrderFilter.values(), default = AdminOrderFilter.ALL)
    }

    private var bint by notNullOnce<MelindaBoobsInterface>()

    suspend fun load(): PageLoadingError? {
        val boobs = makeBoobs()

        bint = boobs.boobsInterface
        boobs.load()?.let {return PageLoadingError(it.error)}
        Globus.world.setPage(Page(header = usualHeader(t("TOTE", "Заказы")),
                                  headerControls = kdiv{o->
                                      o- bint.controlsContent
                                  },
                                  body = bint.mainContent))
        return pageLoadedFineResult
    }

    fun makeBoobs(): MelindaBoobs<UAOrderRTO, AdminOrderFilter, UACreateOrderRequest, UACreateOrderRequest.Response, UAUpdateOrderRequest, UAUpdateOrderRequest.Response> {
        return MelindaBoobs(
            hasCreateButton = false,
            createModalTitle = t("TOTE", "Новый заказ"),
            makeCreateRequest = {UACreateOrderRequest()},
            makeURLAfterCreation = {
                makeURL(pages.uaAdmin.order, listOf())
            },
            makeURLForReload = {boobsParams ->
                makeURL(pages.uaAdmin.order, boobsParams)
            },
            filterValues = AdminOrderFilter.values(),
            defaultFilterValue = AdminOrderFilter.ALL,
            filterSelectKey = selects.adminOrderFilter,
            vaginalInterface = object : MelindaVaginalInterface<UAOrderRTO, AdminOrderFilter, UAUpdateOrderRequest, UAUpdateOrderRequest.Response> {
                suspend override fun sendItemsRequest(req: ItemsRequest<AdminOrderFilter>) = sendUAAdminGetOrders(req)
                override fun shouldShowFilter() = true
                override fun getParentEntityID() = null
                override val humanItemTypeName = t("TOTE", "заказ")
                override fun makeDeleteItemRequest() = UADeleteOrderRequest()
                override fun getItemFromUpdateItemResponse(res: UAUpdateOrderRequest.Response) = res.updatedOrder

                override fun makeUpdateItemRequest(item: UAOrderRTO): UAUpdateOrderRequest {
                    return UAUpdateOrderRequest() - {o ->
                        // TODO:vgrechka ...
                    }
                }

                override fun makeLipsInterface(viewRootID: String, tongue: MelindaTongueInterface<UAOrderRTO>): MelindaLipsInterface {
                    return makeUsualMelindaLips(
                        tongue, viewRootID, bint, icon = fa.folderOpen, initialState = Unit,
                        renderContent = {o ->
                            renderOrderParams(o, tongue.getItem())
                        },
                        titleLinkURL = makeURL(pages.uaAdmin.order, listOf(
                            URLParamValue(UASingleOrderPage.urlQuery.id, tongue.getItem().id)
                        )),
                        hasEditControl = {false},
                        hasDeleteControl = {false}
                    )
                }
            }
        )
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
                    URLParamValue(UASingleOrderPage.urlQuery.id, res.id)
                )))
            }
        )

        m.ignita()
        return null
    }
}


