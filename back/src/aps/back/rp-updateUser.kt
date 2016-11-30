/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*

@RemoteProcedureFactory fun updateUser() = adminProcedure(
    UpdateUserRequest(),

    runShit = {ctx, req ->
/*        #await tx.query(s{y: q`
        update users set
        updated_at = ${requestTimestamp},
        state = ${fields.state},
        profile_rejection_reason = ${fields.state === 'PROFILE_REJECTED' ? fields.profileRejectionReason : null},
        ban_reason = ${fields.state === 'BANNED' ? fields.banReason : null},
        email = ${fields.email},
        kind = ${msg.clientKind},
        first_name = ${fields.firstName},
        last_name = ${fields.lastName},
        admin_notes = ${fields.adminNotes},
        phone = ${fields.phone},
        about_me = ${fields.aboutMe}
        where id = ${msg.id}`})

        #await loadUserForToken(s{})
        return traceEndHandler(s{ret: hunkyDory({newUser: pickFromUser(s{user})})})*/

        ctx.q("Update user")
            .update(USERS)
            .set(USERS.UPDATED_AT, ctx.requestTimestamp)
            .set(USERS.STATE, req.state.value.name)
            .set(USERS.PROFILE_REJECTION_REASON, if (req.state.value == UserState.PROFILE_REJECTED) req.profileRejectionReason.value else null)
            .set(USERS.BAN_REASON, if (req.state.value == UserState.BANNED) req.banReason.value else null)
            .set(USERS.EMAIL, req.immutableSignUpFields.email.value)
            .set(USERS.KIND, ctx.clientKind.name)
            .set(USERS.FIRST_NAME, req.mutableSignUpFields.firstName.value)
            .set(USERS.LAST_NAME, req.mutableSignUpFields.lastName.value)
            .set(USERS.ADMIN_NOTES, req.adminNotes.value)
            .set(USERS.PHONE, req.profileFields.phone.value)
            .set(USERS.ABOUT_ME, req.profileFields.aboutMe.value)
            .where(USERS.ID.eq(req.id.value.toLong()))
            .execute()

        UpdateUserRequest.Response(loadUser(ctx))
    },

    validate = {ctx, req ->
        if (req.state.value == UserState.PROFILE_REJECTED && req.profileRejectionReason.value.isBlank())
            ctx.fieldErrors.add(FieldError(req.profileRejectionReason.name, t("TOTE", "Укажи, почему завернул засранца")))
        if (req.state.value == UserState.BANNED && req.banReason.value.isBlank())
            ctx.fieldErrors.add(FieldError(req.banReason.name, t("TOTE", "Укажи, почему забанил засранца")))
    }
)


