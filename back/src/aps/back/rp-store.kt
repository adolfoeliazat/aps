package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie

@Servant class ServeGetStoreItems : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest()},
            runShit = fun(ctx, req): ItemsResponse<UAOrderRTO> {
                // TODO:vgrechka Security
                return megan(
                    req = req,
                    checkShit = {
                        // TODO:vgrechka ...
                    },
                    table = "ua_orders",
                    itemClass = UAOrder::class.java,
                    addToWhere = {buf, params ->
                        buf += " and order_state = :state"
                        params += MeganQueryParam("state", UAOrderState.IN_STORE.name)

                        val kind = requestUser.kind
                        exhaustive=when (kind) {
                            UserKind.WRITER -> {
                                val filter = req.filter.value.relaxedToEnumOrDie(WriterStoreFilter.values())
                                when (filter) {
                                    WriterStoreFilter.ALL -> {}
                                    WriterStoreFilter.MY_SPECIALIZATION -> {
                                        if (!requestUser.subscribedToAllCategories) {
                                            buf += " and order_category__id in (:cats)"
                                            params += MeganQueryParam("cats", requestUserEntity.documentCategorySubscriptions
                                                .map {it.category.id})
                                        } else Unit
                                    }
                                }
                            }
                            UserKind.ADMIN -> imf("40319ac2-de28-4d0c-be65-8554065d2127")
                            UserKind.CUSTOMER -> wtf("82267ddc-62c1-4984-8414-0276a1ad30ae")
                            else -> {
                                dwarnStriking("fuck")
                            }
                        }
                    }
                )
            }
        ))
    }
}

@Servant class ServeBid : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {BidRequest()},
            runShit = fun(ctx, req: BidRequest): GenericResponse {
                // TODO:vgrechka Security
                checkingAllFieldsRetrieved(req) {
                    val order = uaOrderRepo.findOrDie(req.orderID.value)
                    bidRepo.save(Bid(
                        priceOffer = req.bidPriceOffer.value,
                        durationOffer = req.bidDurationOffer.value,
                        comment = req.bidComment.value,
                        adminNotes = "",
                        order = order,
                        bidder = requestUserEntity
                    ))
                }
                return GenericResponse()
            }
        ))
    }
}

















