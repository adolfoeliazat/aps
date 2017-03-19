package aps.front

import aps.*

class BidsPage {
    suspend fun load() = booby.load()

    val booby: BoobyLoader<BidRTO, /*CreateRequest=*/ Nothing, /*CreateResponse=*/ Nothing, /*UpdateItemRequest=*/ BidRequest, /*UpdateItemResponse=*/ GenericResponse> by mere(BoobyLoader(
        header = t("TOTE", "Стор"),
        makeBoobs = {MelindaBoobs<BidRTO, /*CreateRequest=*/ Nothing, /*CreateResponse=*/ Nothing, /*UpdateItemRequest=*/ BidRequest, /*UpdateItemResponse=*/ GenericResponse>(
            createParams = null,
            makeURLForReload = {boobsParams -> when (user().kind) {
                UserKind.ADMIN -> makeURL(pages.uaAdmin.bids, boobsParams)
                UserKind.WRITER -> wtf("3f3663c9-0eac-4df3-842f-3b4a813a7c93")
                UserKind.CUSTOMER -> wtf("71233608-c84a-4b46-a147-3c0ae0ff0096")
            }},
            filterValues = enumValuesToStringIDTimesTitleList(AdminBidFilter.values()),
            defaultFilterValue = AdminBidFilter.TO_CONSIDER.name,
            filterSelectKey = selects.adminBidFilter,
            vaginalInterface = MelindaVagina<BidRTO, /*UpdateItemRequest=*/ BidRequest, /*UpdateItemResponse=*/ GenericResponse>(
                sendItemsRequest = {req-> sendGetBids(req)},
                shouldShowFilter = {true},
                getParentEntityID = {null},
                humanItemTypeName = t("TOTE", "заявка"),
                makeDeleteItemRequest = {wtf("ba024235-9318-4182-aaf7-5c3ee6b11cb4")},
                updateParams = null,

                makeLipsInterface = {viewRootID, tongue -> makeUsualMelindaLips(
                    viewRootID,
                    searchString = booby.bint.getSearchString(),
                    icon = {fa.usd},
                    initialLipsState = Unit,
                    renderContent = {o->
                        o- renderBid(tongue)
                    },
                    titleLinkURL = makeURL(pages.uaAdmin.bids, listOf(
                        URLParamValue(TabithaURLQuery.id, tongue.item.id)
                    )),
                    getItem = tongue.toItemSupplier()
                )}
            )
        )}
    ))
}


fun renderBid(tongue: MelindaTongueInterface<BidRTO>): ToReactElementable {
    val bid = tongue.item
    val m = MelindaTools
    return kdiv(position = "relative"){o->
        o- kdiv{o->
            o- m.row{o->
                o- m.col(3, fields.bidPriceOffer.title, renderMoney(bid.priceOffer))
                o- m.col(3, fields.bidDurationOffer.title, renderDurationHours(bid.durationOffer))
            }
            o- m.detailsRow(bid.comment, title = t("TOTE", "Комментарий к заявке"), highlightRanges = listOf())
        }
    }
}









