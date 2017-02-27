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
            createParams = MelindaCreateParams(
                hasCreateButton = false,
                createModalTitle = t("TOTE", "Новый заказ"),
                makeCreateRequest = {UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = false)},
                makeURLAfterCreation = {
                    makeURL(pages.uaAdmin.order, listOf())
                }
            ),
            makeURLForReload = {boobsParams ->
                makeURL(pages.uaAdmin.orders, boobsParams)
            },
            filterValues = AdminOrderFilter.values(),
            defaultFilterValue = AdminOrderFilter.ALL,
            filterSelectKey = selects.adminOrderFilter,
            vaginalInterface = MelindaVagina<UAOrderRTO, AdminOrderFilter, UAOrderParamsRequest, UAUpdateOrderResponse>(
                sendItemsRequest = {req-> sendUAAdminGetOrders(req)},
                shouldShowFilter = {true},
                getParentEntityID = {null},
                humanItemTypeName = t("TOTE", "заказ"),
                makeDeleteItemRequest = {UADeleteOrderRequest()},
                updateParams = null,

                makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                    viewRootID,
                    searchString = bint.getSearchString(),
                    icon = {fa.folderOpen},
                    initialLipsState = Unit,
                    renderContent = {o ->
                        o- renderOrderParams(tongue.item)
                    },
                    titleLinkURL = makeURL(pages.uaAdmin.order, listOf(
                        URLParamValue(TabithaURLQuery.id, tongue.item.id)
                    )),
                    getItem = tongue.toItemSupplier()
                )}
            )
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


