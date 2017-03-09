/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*

@Servant class ServeUAAdminGetStuffToDo : BitchyProcedure() {
    override fun serve() {
        fuckAdmin(FuckAdminParams(
            bpc = bpc, makeRequest = {UAAdminGetStuffToDoRequest()},
            runShit = fun(ctx, req): UAAdminGetStuffToDoRequest.Response {
                return UAAdminGetStuffToDoRequest.Response(
                    ordersToApprove = uaOrderRepo.countByOrder_State(UAOrderState.WAITING_ADMIN_APPROVAL),
                    writerProfilesToApprove = userRepo.countByUser_KindAndUser_State(UserKind.WRITER, UserState.PROFILE_APPROVAL_PENDING),
                    bidsToConsider = bidRepo.countByToConsiderByAdmin(true)
                )
            }
        ))
    }
}



