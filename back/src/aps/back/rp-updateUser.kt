/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.UserState.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.*
import into.kommon.*

@RemoteProcedureFactory fun updateUser() = adminProcedure(
    UpdateUserRequest(),

    runShit = {ctx, req ->
        val oldUser = ctx.loadUser(req.id.value.toLong())

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

        val newUser = ctx.q("Select user")
            .select().from(USERS)
            .where(USERS.ID.eq(req.id.value.toLong()))
            .fetchOne().into(JQUsers::class.java).toRTO(ctx.q)

        if (oldUser.state != COOL && newUser.state == COOL) {
            val dashboardURL = "http://${ctx.clientDomain}${ctx.clientPortSuffix}/dashboard.html"
            EmailMatumba.send(Email(
                to = "${newUser.firstName} ${newUser.lastName} <${newUser.email}>",
                subject = when (ctx.lang) {
                    Language.UA -> when (ctx.clientKind) {
                        ClientKind.CUSTOMER -> "Тебя пустили на APS"
                        ClientKind.WRITER -> "Тебя пустили на Writer UA"
                    }
                    Language.EN -> imf("EN profile coolification email")
                },
                html = dedent(t(
                    en = """
                        TODO
                    """,
                    ua = """
                        Привет, ${newUser.firstName}!<br><br>
                        Тебя пустили на сайт, заходи и пользуйся. Только не шали.
                        <br><br>
                        <a href="$dashboardURL">$dashboardURL</a>
                    """
                ))
            ))
        }

        UpdateUserRequest.Response(loadUser(ctx))
    },

    validate = {ctx, req ->
        if (req.state.value == UserState.PROFILE_REJECTED && req.profileRejectionReason.value.isBlank())
            ctx.fieldErrors.add(FieldError(req.profileRejectionReason.name, t("TOTE", "Укажи, почему завернул засранца")))
        if (req.state.value == UserState.BANNED && req.banReason.value.isBlank())
            ctx.fieldErrors.add(FieldError(req.banReason.name, t("TOTE", "Укажи, почему забанил засранца")))
    }
)


