/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import into.kommon.*
import org.mindrot.jbcrypt.BCrypt
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

            ctx.q("Insert user")
                .insertInto(USERS)
                .set(USERS.INSERTED_AT, ctx.requestTimestamp)
                .set(USERS.UPDATED_AT, ctx.requestTimestamp)
                .set(USERS.EMAIL, email)
                .set(USERS.KIND, ctx.clientKind.name)
                .set(USERS.LANG, ctx.lang.name)
                .set(USERS.STATE,
                     when(ctx.clientKind) {
                         ClientKind.CUSTOMER -> UserState.COOL.name
                         ClientKind.WRITER -> UserState.PROFILE_PENDING.name
                     })
                .set(USERS.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
                .set(USERS.FIRST_NAME, firstName)
                .set(USERS.LAST_NAME, lastName)
                .execute()

            val signInURL = "http://${ctx.clientDomain}${ctx.clientPortSuffix}/sign-in.html" // TODO:vgrechka Use HTTPS    a389374b-8132-44e9-a73b-fde56869d690

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
            throw e
//            if (e.code === '23505') {
//                fieldErrors.email = t('This email is already registered', 'Такая почта уже зарегистрирована')
//                return traceEndHandler(s{ret: fixErrorsResult()})
//            } else {
//                throw e
//            }
        }
    },

    validate = {ctx, req ->
        if (req.agreeTerms.no) {
            ctx.fieldErrors.add(FieldError(req.agreeTerms.name, t("You have to agree with terms and conditions", "Необходимо принять соглашение")))
        }
    }
)


