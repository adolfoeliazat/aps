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

@RemoteProcedureFactory
fun signUp() = publicProcedure(
    SignUpRequest(),
    runShit = fun(ctx, req): GenericResponse {
        try {
            val firstName = req.mutableSignUpFields.firstName.value
            val lastName = req.mutableSignUpFields.lastName.value
            val email = req.immutableSignUpFields.email.value
            val password = TestServerFiddling.nextGeneratedPassword?.let {
                TestServerFiddling.nextGeneratedPassword = null
                it
            } ?: "" + UUID.randomUUID()

            USERS.let {
                ctx.insertShit("Insert user", it)
                    .set(it.INSERTED_AT, ctx.requestTimestamp)
                    .set(it.UPDATED_AT, ctx.requestTimestamp)
                    .set(it.EMAIL, email)
                    .set(it.KIND, when (ctx.clientKind) {
                        ClientKind.CUSTOMER -> JQUserKind.CUSTOMER
                        ClientKind.WRITER -> JQUserKind.WRITER
                    })
                    .set(it.LANG, ctx.lang.name)
                    .set(it.STATE, when(ctx.clientKind) {
                        ClientKind.CUSTOMER -> UserState.COOL.name
                        ClientKind.WRITER -> UserState.PROFILE_PENDING.name
                    })
                    .set(it.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
                    .set(it.FIRST_NAME, firstName)
                    .set(it.LAST_NAME, lastName)
                    .set(it.ADMIN_NOTES, "")
                    .execute()
            }

            val signInURL = "${requestShit.commonRequestFields.clientURL}/sign-in.html"

            EmailMatumba.send(Email(
                to = "$firstName $lastName <$email>",
                subject = when (ctx.lang) {
                    Language.UA -> when (ctx.clientKind) {
                        ClientKind.CUSTOMER -> "Пароль для APS"
                        ClientKind.WRITER -> "Пароль для Writer UA"
                    }
                    Language.EN -> when (ctx.clientKind) {
                        ClientKind.CUSTOMER -> imf("Email title for en-customer")
                        ClientKind.WRITER -> imf("Email title for en-writer")
                    }
                },
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
            return GenericResponse()
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


