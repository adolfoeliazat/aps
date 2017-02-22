/*
 * APS
 *
 * (C) Copyright 2015-2017 Vladimir Grechka
 */

package aps.back

import aps.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@Servant class ServeSignInWithPassword(val userRepo: UserRepository, val tokenRepo: UserTokenRepository) : BitchyProcedure() {
    override fun serve() {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts
        // TODO:vgrechka Prevent things like writer signing into customer-facing site
        // TODO:vgrechka Store tokens in Redis instead of DB

        fuckAnonymous(FuckAnonymousParams(
            bpc = bpc, makeRequest = {SignInWithPasswordRequest()},
            runShit = fun(ctx, req: SignInWithPasswordRequest): SignInResponse {
                val vagueFuckYouMessage = t("Invalid email or password", "Неверная почта или пароль")
                val user = userRepo.findByEmail(req.email.value) ?: bitchExpectedly(vagueFuckYouMessage)

                if (!BCrypt.checkpw(req.password.value, user.passwordHash)) bitchExpectedly(vagueFuckYouMessage)

                val token = generateUserToken()
                tokenRepo.save(UserToken(token, user))
                return SignInResponse(token, user.toRTO(searchWords = listOf()))
            }
        ))
    }
}


@Servant class ServeSignInWithToken : BitchyProcedure() {
    override fun serve() {
        fuckAnyUser(FuckAnyUserParams(
            bpc = bpc,
            makeRequest = {SignInWithTokenRequest()},
            runShit = fun(ctx, req: SignInWithTokenRequest): SignInResponse {
                return SignInResponse(ctx.token, ctx.user_killme)
            }
        ))
    }
}

















//@RemoteProcedureFactory fun signInWithToken() = anyUserProcedure(
//    {SignInWithTokenRequest()},
//    wrapInFormResponse = false,
//    runShit = fun(ctx, req): SignInResponse {
//        return SignInResponse(ctx.token, ctx.user_killme)
//    }
//)


//@RemoteProcedureFactory fun signInWithPassword() = publicProcedure(
//    {SignInWithPasswordRequest()},
//    runShit = fun(ctx, req): SignInResponse {
//        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
//
//        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
//
//        val users = tracingSQL("Select user") {ctx.q
//            .select().from(USERS)
//            .where(USERS.EMAIL.equal(req.email.value))
//            .fetch().into(JQUsers::class.java)
//        }
//        if (users.isEmpty()) bitchExpectedly(vagueMessage)
//
//        val user = users[0]
//        if (!BCrypt.checkpw(req.password.value, user.passwordHash)) bitchExpectedly(vagueMessage)
//
//        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
//
//        // TODO:vgrechka Store tokens in Redis instead of DB    c51fe75c-f55e-4a68-9a7b-465e44db6235
//        val token = "" + UUID.randomUUID()
//        tracingSQL("Insert token") {ctx.q
//            .insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
//            .values(user.id, token)
//            .execute()
//        }
//
//        // TODO:vgrechka Load related user shit?
//
//        return SignInResponse(token, user.toRTO(ctx.q))
//    }
//)

