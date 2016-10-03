/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import aps.back.MatumbaProcedure.*
import aps.back.generated.jooq.tables.pojos.Users

@RemoteProcedureFactory
fun getLiveStatus() = MatumbaProcedure(GetLiveStatusRequest(), GetLiveStatusRequest.Response()) {
    access = Access.USER
    softenShit = false

    runShit = {
        res.case = when (user.kind) {
            UserKind.ADMIN -> GetLiveStatusRequest.Response.Case.ForAdmin(
                profilesToApprove = "" + q
                    .selectCount()
                    .from(USERS)
                    .where(USERS.STATE.eq(UserState.PROFILE_APPROVAL_PENDING.name))
                    .and(USERS.ASSIGNED_TO.eq(user.id.toLong()))
                    .fetchOne(0, java.lang.Long.TYPE),
                suka = "blia-" + puid()
            )
            UserKind.WRITER -> GetLiveStatusRequest.Response.Case.ForWriter(
                suka = "blia-" + puid()
            )
            UserKind.CUSTOMER -> GetLiveStatusRequest.Response.Case.ForCustomer(
                suka = "blia-" + puid()
            )
        }
    }
}


