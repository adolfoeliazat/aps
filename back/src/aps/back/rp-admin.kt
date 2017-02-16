/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*

@Servant class ServeUAAdminGetStuffToDo(val orderRepo: UAOrderRepository) : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc, makeRequest = {UAAdminGetStuffToDoRequest()},
            runShit = fun(ctx, req): UAAdminGetStuffToDoRequest.Response {
                return UAAdminGetStuffToDoRequest.Response(
                    ordersToApprove = orderRepo.countByState(UAOrderState.WAITING_ADMIN_APPROVAL)
                )
            }
        ))
    }
}

