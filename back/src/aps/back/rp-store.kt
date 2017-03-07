package aps.back

import aps.*
import into.kommon.*

@Servant class ServeGetStoreItems : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(filterValues = when (requestUser.kind) {
                UserKind.WRITER -> StoreFilter.values()
//                UserKind.WRITER -> WriterStoreFilter.values()
                UserKind.ADMIN -> imf("ed999540-edf0-4ed0-a220-ba5b2d2a8689")
                UserKind.CUSTOMER -> wtf("3b32cd38-599b-43b1-8cc4-9d24b5e924f5")
            })},
            runShit = fun(ctx, req): ItemsResponse<UAOrderRTO> {
                // TODO:vgrechka Security
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
//                            WriterStoreFilter.ALL -> {}
//                            WriterStoreFilter.MY_SPECIALIZATION -> imf("871a3c22-e02a-4c7d-b936-21fa91f398ef")
                        }
                    }
                )
            }
        ))
    }
}


