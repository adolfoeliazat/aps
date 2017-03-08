package aps.back

import aps.*
import into.kommon.*

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
                    addToWhere = {s, params ->
                        s += " and order_state = :state"
                        params += MeganQueryParam("state", UAOrderState.IN_STORE.name)

                        exhaustive/when (requestUser.kind) {
                            UserKind.WRITER -> {
                                val filter = req.filter.value.relaxedToEnum(WriterStoreFilter.values(), WriterStoreFilter.ALL)
                                exhaustive/when (filter) {
                                    WriterStoreFilter.ALL -> {}
                                    WriterStoreFilter.MY_SPECIALIZATION -> imf("871a3c22-e02a-4c7d-b936-21fa91f398ef")
                                }
                            }
                            UserKind.ADMIN -> imf("40319ac2-de28-4d0c-be65-8554065d2127")
                            UserKind.CUSTOMER -> wtf("82267ddc-62c1-4984-8414-0276a1ad30ae")
                        }
                    }
                )
            }
        ))
    }
}


