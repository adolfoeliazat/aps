/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import aps.back.generated.jooq.enums.*
import into.kommon.*
import org.jooq.exception.DataAccessException
import org.mindrot.jbcrypt.BCrypt
import org.postgresql.util.PSQLException
import java.sql.SQLException
import java.util.*

@RemoteProcedureFactory fun serveSignUp() = publicProcedure(
    {SignUpRequest()},
    runShit = fun(ctx, req): SignUpRequest.Response {
        try {
            val firstName = req.mutableSignUpFields.firstName.value
            val lastName = req.mutableSignUpFields.lastName.value
            val email = req.immutableSignUpFields.email.value
            val password = TestServerFiddling.nextGeneratedPassword.getAndReset()
                ?: UUID.randomUUID().toString()

            val userID = USERS.let {t->
                ctx.insertShit("Insert user", t) {it
                    .set(t.INSERTED_AT, RequestGlobus.stamp)
                    .set(t.UPDATED_AT, RequestGlobus.stamp)
                    .set(t.EMAIL, email)
                    .set(t.KIND, when (ctx.clientKind) {
                        ClientKind.UA_CUSTOMER -> JQUserKind.CUSTOMER
                        ClientKind.UA_WRITER -> JQUserKind.WRITER
                    })
                    .set(t.LANG, ctx.lang.name)
                    .set(t.STATE, when(ctx.clientKind) {
                        ClientKind.UA_CUSTOMER -> UserState.COOL.name
                        ClientKind.UA_WRITER -> UserState.PROFILE_PENDING.name
                    })
                    .set(t.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
                    .set(t.FIRST_NAME, firstName)
                    .set(t.LAST_NAME, lastName)
                    .set(t.ADMIN_NOTES, "")
                    .returnID(t)
                }
            }

            val signInURL = "${RequestGlobus.commonRequestFields.clientURL}/sign-in.html"

            EmailMatumba.send(Email(
                to = "$firstName $lastName <$email>",
                subject = when (ctx.clientKind) {
                    ClientKind.UA_CUSTOMER -> "Пароль для APS"
                    ClientKind.UA_WRITER -> "Пароль для Writer UA"
                },
//                subject = when (ctx.lang) {
//                    Language.UA -> when (ctx.clientKind) {
//                        ClientKind.CUSTOMER -> "Пароль для APS"
//                        ClientKind.WRITER -> "Пароль для Writer UA"
//                    }
//                    Language.EN -> when (ctx.clientKind) {
//                        ClientKind.CUSTOMER -> imf("Email title for en-customer")
//                        ClientKind.WRITER -> imf("Email title for en-writer")
//                    }
//                },
                html = dedent(t(
                    en = """
                        TODO
                    """,
                    ua = """
                        Привет, $firstName!<br><br>
                        Вот твой пароль: ${password}
                        <br><br>
                        <a href="${signInURL}">${signInURL}</a>
                    """
                ))
            ))
            return SignUpRequest.Response(userID.toString())
        } catch (e: Throwable) {
            if (e is DataAccessException) {
                val sqlState = e.sqlState()
                dlog("SQL state: ", sqlState)
                if (sqlState == "23505")
                    bitchExpectedly(t("This email is already registered", "Эта почта уже занята"))
            }
            throw e
        }
    },

    validate = {ctx, req ->
        if (req.agreeTerms.no) {
            ctx.fieldErrors.add(FieldError(req.agreeTerms.name, t("You have to agree with terms and conditions", "Необходимо принять соглашение")))
        }
    }
)


