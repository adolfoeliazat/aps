/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.USERS
import aps.GetLiveStatusRequest.Response as Res

@RemoteProcedureFactory fun getLiveStatus() = anyUserProcedure(
    GetLiveStatusRequest(),
    wrapInFormResponse = false,
    runShit = {ctx, req ->
        when (ctx.user.kind) {
            UserKind.ADMIN -> Res.ForAdmin(
                profilesToApprove = ctx.q("Select amount of profiles to approve")
                    .selectCount().from(USERS)
                    .where(USERS.STATE.eq(UserState.PROFILE_APPROVAL_PENDING.name))
                    .and(USERS.ASSIGNED_TO.eq(ctx.user.id.toLong()))
                    .fetchOne(0, java.lang.Long.TYPE).toString(),
                suka = "blia-" + puid()
            )
            UserKind.WRITER -> Res.ForWriter(
                suka = "blia-" + puid()
            )
            UserKind.CUSTOMER -> Res.ForCustomer(
                suka = "blia-" + puid()
            )
        }
    }
)

