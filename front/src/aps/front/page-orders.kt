package aps.front

import aps.*
import into.kommon.*

class UAAdminOrdersPage {
    private var bint by notNullOnce<MelindaBoobsInterface>()

    // TODO:vgrechka Deduplicate    f5af6cdf-3fa1-4b09-985e-f949e187fa60
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

    fun makeBoobs(): MelindaBoobs<UAOrderRTO, AdminOrderFilter, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse> {
        return MelindaBoobs(
            hasCreateButton = false,
            createModalTitle = t("TOTE", "Новый заказ"),
            makeCreateRequest = {UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = false)},
            makeURLAfterCreation = {
                makeURL(pages.uaAdmin.order, listOf())
            },
            makeURLForReload = {boobsParams ->
                makeURL(pages.uaAdmin.orders, boobsParams)
            },
            filterValues = AdminOrderFilter.values(),
            defaultFilterValue = AdminOrderFilter.ALL,
            filterSelectKey = selects.adminOrderFilter,
            vaginalInterface = object:MelindaVaginalInterface<UAOrderRTO, AdminOrderFilter, UAOrderParamsRequest, UAUpdateOrderResponse> {
                suspend override fun sendItemsRequest(req: ItemsRequest<AdminOrderFilter>) = sendUAAdminGetOrders(req)
                override fun shouldShowFilter() = true
                override fun getParentEntityID() = null
                override val humanItemTypeName = t("TOTE", "заказ")
                override fun makeDeleteItemRequest() = UADeleteOrderRequest()
                override fun getItemFromUpdateItemResponse(res: UAUpdateOrderResponse) = wtf("Order should not be edited through vaginal interface    ba7ab79e-e99a-49b9-9dfa-2d4a452b23b0")
                override val updateItemProcedureNameIfNotDefault get()= wtf("Order should not be edited through vaginal interface    19ca13a9-7432-4c33-aff3-0e9e9026b09d")

                override fun makeUpdateItemRequest(item: UAOrderRTO): UAOrderParamsRequest {
                    return UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = true).populateCheckingCompleteness{o->
                        // TODO:vgrechka ...
                        populateWithAdminNotes(o, item)
                    }
                }

                override fun makeLipsInterface(viewRootID: String, tongue: MelindaTongueInterface<UAOrderRTO>): MelindaLipsInterface {
                    return makeUsualMelindaLips(
                        tongue, viewRootID, bint, icon = {fa.folderOpen}, initialState = Unit,
                        renderContent = {o ->
                            o- renderOrderParams(tongue.getItem())
                        },
                        titleLinkURL = makeURL(pages.uaAdmin.order, listOf(
                            URLParamValue(TabithaURLQuery.id, tongue.getItem().id)
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
        imf("")
//        val m = Pizdalinda<UAOrderRTO, Nothing, CustomerOrderFilter>(
//            ui = world,
//            urlPath = "orders.html",
//            procedureName = "customerGetOrders",
//            header = {t("My Orders", "Мои заказы")},
//            filterSelectValues = CustomerOrderFilter.values(),
//            defaultFilter = CustomerOrderFilter.ALL,
//
//            renderItem = {index, order ->
//                kdiv {o ->
//                    o - "pizda"
//                }
//            }
//        )
//
//        m.specifyPlus(
//            plusFormSpec = FormSpec<UAOrderParamsRequest, UAOrderParamsRequest.Response>(
//                req = UAOrderParamsRequest(),
//                ui = world,
//                primaryButtonTitle = t("Create", "Создать"),
//                cancelButtonTitle = const.text.shebang.defaultCancelButtonTitle),
//            onPlusFormSuccessa = {res->
//                world.pushNavigate(makeURL(pages.uaCustomer.order, listOf(
//                    URLParamValue(UASingleOrderPage.urlQuery.id, res.orderID)
//                )))
//            }
//        )
//
//        m.ignita()
        return null
    }
}


