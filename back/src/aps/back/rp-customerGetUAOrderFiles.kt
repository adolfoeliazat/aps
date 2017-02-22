/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
import into.kommon.*
import org.springframework.data.repository.findOrDie

@Servant class ServeUAAdminGetOrders : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(AdminOrderFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UAOrderRTO> {
                return megan(
                    req = req,
                    checkShit = {
                        // TODO:vgrechka ...
                    },
                    table = "ua_orders",
                    itemClass = UAOrder::class.java,
                    addToWhere = {s, params ->
                        exhaustive/when (req.filter.value) {
                            AdminOrderFilter.ALL -> {}
                            AdminOrderFilter.TO_APPROVE -> {
                                s += " and state = :state"
                                params += MeganQueryParam("state", UAOrderState.WAITING_ADMIN_APPROVAL.name)
                            }
                        }
                    }
                )
            }
        ))
    }
}

@Servant class ServeUACustomerGetOrderFiles(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {ItemsRequest(CustomerFileFilter.values())},
            runShit = fun(ctx, req): ItemsResponse<UAOrderFileRTO> {
                return megan(
                    req = req,
                    checkShit = {
                        val order = orderRepo.findOrDie(req.parentEntityID.value!!)
                        // TODO:vgrechka @security Check permissions
                        // TODO:vgrechka Friendly (like "fuck you" or something) message if no such order (user may mistype URL, etc.)
                    },
                    table = "ua_order_files",
                    itemClass = UAOrderFile::class.java,
                    parentKey = "orderID",
                    addToWhere = {s, params ->
                        exhaustive/when (req.filter.value) {
                            CustomerFileFilter.ALL -> {}
                            CustomerFileFilter.FROM_ME -> imf("41654446-347a-4d51-b27b-bb5d9a750820")
                            CustomerFileFilter.FROM_WRITER -> imf("537f0e0c-77f4-483e-8f5a-c29cfcfdfd5b")
                            CustomerFileFilter.FROM_SUPPORT -> imf("20c4c6ab-1c90-4db3-b29b-f06059e676b0")
                        }
                    }
                )
            }
        ))
    }
}




















