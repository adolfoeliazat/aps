package aps.back

import aps.*

@Servant class ServeGetStoreItems : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(StoreFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UAOrderRTO> {
                return megan(
                    req = req,
                    checkShit = {
                        // TODO:vgrechka ...
                    },
                    table = "ua_orders",
                    itemClass = UAOrder::class.java,
                    addToWhere = {s, params ->
                        s += " and order_state = :state"
                        params += MeganQueryParam("state", UAOrderState.IN_STORE.name)

                        exhaustive/when (req.filter.value) {
                            StoreFilter.ALL -> {}
                        }
                    }
                )
            }
        ))
    }
}


