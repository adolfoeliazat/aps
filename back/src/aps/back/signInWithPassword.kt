/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.tables.pojos.Users
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import aps.back.MatumbaProcedure.*

@RemoteProcedureFactory
fun signInWithPassword() = MatumbaProcedure(SignInWithPasswordRequest(), SignInWithPasswordRequest.Response()) {
    access = Access.PUBLIC

    runShit = {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4

        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")

        val users = q.select().from(USERS).where(USERS.EMAIL.equal(req.email.value)).fetch().into(Users::class.java)
        if (users.isEmpty()) bitchExpectedly(vagueMessage)

        val user = users[0]
        if (!BCrypt.checkpw(req.password.value, user.passwordHash)) bitchExpectedly(vagueMessage)

        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157

        // TODO:vgrechka Store tokens in Redis instead of DB    c51fe75c-f55e-4a68-9a7b-465e44db6235
        res.token = "" + UUID.randomUUID()
        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
            .values(user.id, res.token)
            .execute()

        // TODO:vgrechka Load related user shit?

        res.user = user.toRTO(q)
    }
}

//class SignInWithPasswordRemoteProcedure : RemoteProcedure<Request, SignInWithPasswordResponse>() {
//    override val access = Access.PUBLIC
//
//    val email = emailField()
//    val password = passwordField()
//
//    override fun doStuff() {
//        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts    5e8dd00b-c96e-4991-b350-a1aa78c784a4
//
//        val vagueMessage = t("Invalid email or password", "Неверная почта или пароль")
//
//        val users = q.select().from(USERS).where(USERS.EMAIL.equal(email.value)).fetch().into(Users::class.java)
//        if (users.isEmpty()) bitchExpectedly(vagueMessage)
//
//        val user = users[0]
//        if (!BCrypt.checkpw(password.value, user.passwordHash)) bitchExpectedly(vagueMessage)
//
//        // TODO:vgrechka Prevent things like writer signing into customer-facing site    69781de0-05c4-440f-98ac-6de6e0c31157
//
//        // TODO:vgrechka Store tokens in Redis instead of DB    c51fe75c-f55e-4a68-9a7b-465e44db6235
//        res.token = "" + UUID.randomUUID()
//        q.insertInto(USER_TOKENS, USER_TOKENS.USER_ID, USER_TOKENS.TOKEN)
//            .values(user.id, res.token)
//            .execute()
//
//        // TODO:vgrechka Load related user shit?
//
//        res.user = user.toRTO()
//    }
//}

