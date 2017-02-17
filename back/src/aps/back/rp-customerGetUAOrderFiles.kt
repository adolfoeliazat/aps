/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
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
                    itemClass = UAOrder::class.java
                )
            }
        ))
    }
}

@Servant class ServeUACustomerGetOrderFiles(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckCustomer(FuckCustomerParams(
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
                    parentKey = "orderID"
                )
            }
        ))
    }
}




















