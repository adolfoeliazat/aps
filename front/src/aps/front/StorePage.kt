package aps.front

import aps.*
import into.kommon.*

class StorePage {
    suspend fun load() = booby.load()

    val booby: BoobyLoader<UAOrderRTO, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse> by mere(BoobyLoader(
        header = t("TOTE", "Стор"),
        makeBoobs = {MelindaBoobs<UAOrderRTO, UAOrderParamsRequest, UACreateOrderResponse, UAOrderParamsRequest, UAUpdateOrderResponse>(
            createParams = MelindaCreateParams(
                hasCreateButton = false,
                createModalTitle = t("TOTE", "Новый заказ"),
                makeCreateRequest = {UAOrderParamsRequest(isAdmin = isAdmin(), isUpdate = false)},
                makeURLAfterCreation = {
                    makeURL(pages.uaAdmin.order, listOf())
                }
            ),
            makeURLForReload = {boobsParams -> when (user().kind) {
                UserKind.WRITER -> makeURL(pages.uaWriter.store, boobsParams)
                UserKind.CUSTOMER -> imf("620d20fd-86ec-4cce-9bc4-efd396b46f72")
                UserKind.ADMIN -> imf("8eef9765-5cf4-4663-b755-dbf4980e666e")
            }},
            filterValues = enumValuesToStringIDTimesTitleList(WriterStoreFilter.values()),
            defaultFilterValue = WriterStoreFilter.MY_SPECIALIZATION.name,
            filterSelectKey = selects.writerStoreFilter,
            vaginalInterface = MelindaVagina<UAOrderRTO, UAOrderParamsRequest, UAUpdateOrderResponse>(
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
                        o- renderStoreItem(tongue)
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


fun renderStoreItem(tongue: MelindaTongueInterface<UAOrderRTO>): ToReactElementable {
    val order = tongue.item
    val myBid = order.myBid
    val m = MelindaTools
    return kdiv(position = "relative"){o->
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

        if (myBid != null) {
            o- kdiv(className = css.myBidInStoreItem){o->
                o- m.row{o->
                    o- m.col(6, fields.bidPriceOffer.title, renderMoney(myBid.priceOffer))
                    o- m.col(6, fields.bidDurationOffer.title, renderDurationHours(myBid.durationOffer))
                }
                o- m.detailsRow(myBid.comment, title = t("TOTE", "Комментарий к заявке"), highlightRanges = listOf())
            }
        }

        o- m.detailsRow(order.details, order.detailsHighlightRanges, title = fields.orderDetails.title)
        o- renderAdminNotesIfNeeded(order)


        if (myBid == null) {
            o- Button(icon = fa.usd, title = t("TOTE", "Дайте мне"), className = css.bidButton, key = buttons.bid) {
                openEditModal(
                    title = t("TOTE", "Заявка на выполнение"),
                    formSpec = FormSpec<BidRequest, GenericResponse>(
                        primaryButtonTitle = t("TOTE", "Да!"),
                        cancelButtonTitle = t("TOTE", "Та не..."),
                        req = BidRequest()-{o->
                            o.orderID.value = order.id
                        }
                    ),
                    onSuccessBeforeClosingModal = {
                        showingModalIfShittyResponse({reginaLoadUAOrder(order.id)}) {
                            tongue.replaceItem(it)
                        }
                    }
                )
            }
        }
    }
}




