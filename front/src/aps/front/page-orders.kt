package aps.front

import aps.*

class UAAdminOrdersPage {
    suspend fun load() = booby.load()

    val booby: BoobyLoader<UAOrderRTO, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse> by mere(BoobyLoader(
        header = t("TOTE", "Заказы"),
        makeBoobs = {MelindaBoobs<UAOrderRTO, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse>(
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
            filterValues = enumValuesToStringIDTimesTitleList(AdminOrderFilter.values()),
            defaultFilterValue = AdminOrderFilter.ALL.name,
            filterSelectKey = selects.adminOrderFilter,
            vaginalInterface = MelindaVagina<UAOrderRTO, UAOrderParamsRequest, UAUpdateOrderResponse>(
                sendItemsRequest = {req-> sendUAAdminGetOrders(req)},
                shouldShowFilter = {true},
                getParentEntityID = {null},
                humanItemTypeName = t("TOTE", "заказ"),
                makeDeleteItemRequest = {UADeleteOrderRequest()},
                updateParams = null,

                makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                    viewRootID,
                    searchString = booby.bint.getSearchString(),
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
        )}
    ))
}




