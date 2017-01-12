/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.JQUsers
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@RemoteProcedureFactory fun signInWithPassword() = publicProcedure(
    SignInWithPasswordRequest(),
    runShit = fun(ctx, req): SignInResponse {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4

        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")

        val users = ctx.qshit("Select user")
            .select().from(USERS)
            .where(USERS.EMAIL.equal(req.email.value))
            .fetch().into(JQUsers::class.java)
        if (users.isEmpty()) bitchExpectedly(vagueMessage)

        val user = users[0]
        if (!BCrypt.checkpw(req.password.value, user.passwordHash)) bitchExpectedly(vagueMessage)

        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157

        // TODO:vgrechka Store tokens in Redis instead of DB    c51fe75c-f55e-4a68-9a7b-465e44db6235
        val token = "" + UUID.randomUUID()
        ctx.qshit("Insert token")
            .insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
            .values(user.id, token)
            .execute()

        // TODO:vgrechka Load related user shit?

        return SignInResponse(token, user.toRTO(ctx.qshit))
    }
)


@RemoteProcedureFactory fun signInWithToken() = anyUserProcedure(
    SignInWithTokenRequest(),
    wrapInFormResponse = false,
    runShit = fun(ctx, req): SignInResponse {
        return SignInResponse(ctx.token, ctx.user)
    }
)


