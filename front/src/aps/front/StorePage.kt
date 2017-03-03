package aps.front

import aps.*
import into.kommon.*

class StorePage {
    suspend fun load() = booby.load()

    val booby: BoobyLoader<UAOrderRTO, StoreFilter, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse> by mere(BoobyLoader(
        header = t("TOTE", "Стор"),
        makeBoobs = {MelindaBoobs<UAOrderRTO, StoreFilter, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse>(
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
            filterValues = StoreFilter.values(),
            defaultFilterValue = StoreFilter.ALL,
            filterSelectKey = selects.storeFilter,
            vaginalInterface = MelindaVagina<UAOrderRTO, StoreFilter, UAOrderParamsRequest, UAUpdateOrderResponse>(
                sendItemsRequest = {req-> sendGetStoreItems(req)},
                shouldShowFilter = {true},
                getParentEntityID = {null},
                humanItemTypeName = t("TOTE", "заказ"),
                makeDeleteItemRequest = {wtf("01932c57-f485-444a-9861-d5b25f1446f2")},
                updateParams = null,

                makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                    viewRootID,
                    searchString = booby.bint.getSearchString(),
                    icon = {fa.folderOpen},
                    initialLipsState = Unit,
                    renderContent = {o->
                        o- renderStoreItem(tongue.item)
                    },
                    titleLinkURL = makeURL(pages.uaAdmin.order, listOf( // TODO:vgrechka ....................
                        URLParamValue(TabithaURLQuery.id, tongue.item.id)
                    )),
                    getItem = tongue.toItemSupplier()
                )}
            )
        )}
    ))
}


fun renderStoreItem(order: UAOrderRTO): ToReactElementable {
    val m = MelindaTools
    return kdiv{o->
        o- m.row{o->
            o- m.col(3, t("TOTE", "Попал в стор")){o->
                o- formatUnixTime(bang(order.movedToStoreAt))
            }
            o- m.col(9, t("TOTE", "Категория")){o->
                o- order.documentCategory.pathTitle
            }
        }

        o- m.row{o->
            o- m.col(3, fields.uaDocumentType.title, order.documentType.title)
            o- m.col(3, fields.numPages.title, order.numPages.toString())
            o- m.col(3, fields.numSources.title, order.numSources.toString())
        }

        o- renderOrderStoreBoundaries(order)
        o- m.detailsRow(order.details, order.detailsHighlightRanges, title = fields.orderDetails.title)
        o- renderAdminNotesIfNeeded(order)
    }
}

