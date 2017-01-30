/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.back.generated.jooq.Tables.*
import aps.*
import aps.back.generated.jooq.tables.pojos.JQUsers

@RemoteProcedureFactory fun updateProfile() = anyUserProcedure(
    {UpdateProfileRequest()},
    runShit = fun(ctx, req): UpdateProfileRequest.Response {
        val q = tracingSQL("Update profile") {ctx.q
            .update(USERS)
            .set(USERS.PROFILE_UPDATED_AT, ctx.requestTimestamp)
            .set(USERS.PHONE, req.profileFields.phone.value)
            .set(USERS.COMPACT_PHONE, compactPhone(req.profileFields.phone.value))
            .set(USERS.ABOUT_ME, req.profileFields.aboutMe.value)
            .set(USERS.STATE, UserState.PROFILE_APPROVAL_PENDING.name)
            .set(USERS.ASSIGNED_TO, THE_ADMIN_ID)
            .where(USERS.ID.eq(ctx.user.id.toLong()))
            .execute()
        }

        return UpdateProfileRequest.Response(loadUser(ctx))
    }
)


