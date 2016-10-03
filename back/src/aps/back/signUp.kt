/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*
import aps.back.generated.jooq.Tables.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import aps.back.MatumbaProcedure.*

@RemoteProcedureFactory
fun signUp() = MatumbaProcedure(SignUpRequest(), GenericResponse()) {
    access = Access.PUBLIC

    validate = {
        if (req.agreeTerms.no) {
            fieldErrors.add(FieldError("agreeTerms", t("You have to agree with terms and conditions", "Необходимо принять соглашение")))
        }
    }

    runShit = {
        try {
            val firstName = req.signUpFields.firstName.value
            val lastName = req.signUpFields.lastName.value
            val email = req.signUpFields.email.value
            val password = TestServerFiddling.nextGeneratedPassword?.let {
                TestServerFiddling.nextGeneratedPassword = null
                it
            } ?: "" + UUID.randomUUID()

            q.insertInto(USERS)
                .set(USERS.INSERTED_AT, requestTimestamp)
                .set(USERS.UPDATED_AT, requestTimestamp)
                .set(USERS.EMAIL, email)
                .set(USERS.KIND, clientKind.name)
                .set(USERS.LANG, lang.name)
                .set(USERS.STATE, UserState.PROFILE_PENDING.name)
                .set(USERS.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
                .set(USERS.FIRST_NAME, firstName)
                .set(USERS.LAST_NAME, lastName)
                .execute()


            val signInURL = "http://${clientDomain}${clientPortSuffix}/sign-in.html" // TODO:vgrechka Use HTTPS    a389374b-8132-44e9-a73b-fde56869d690

            EmailMatumba.send(Email(
                to = "$firstName $lastName <$email>",
                subject = when (lang) {
                    Language.UA -> when (clientKind) {
                        ClientKind.CUSTOMER -> "Пароль для APS"
                        ClientKind.WRITER -> "Пароль для Writer UA"
                    }
                    Language.EN -> when (clientKind) {
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
        } catch (e: Throwable) {
            throw e
//            if (e.code === '23505') {
//                fieldErrors.email = t('This email is already registered', 'Такая почта уже зарегистрирована')
//                return traceEndHandler(s{ret: fixErrorsResult()})
//            } else {
//                throw e
//            }
        }
    }
}

//class SignUpRemoteProcedure : RemoteProcedure<Request, GenericResponse>() {
//    override val access = Access.PUBLIC
//
//    val email = emailField()
//    val firstName = textField("firstName", 3, 50)
//    val lastName = textField("lastName", 3, 50)
//    val agreeTerms = agreeTermsField()
//
//    override fun validate() {
//        if (agreeTerms.no) {
//            fieldErrors.add(FieldError("agreeTerms", t("You have to agree with terms and conditions", "Необходимо принять соглашение")))
//        }
//    }
//
//    override fun doStuff() {
//        try {
//            val password = TestServerFiddling.nextGeneratedPassword?.let {
//                TestServerFiddling.nextGeneratedPassword = null
//                it
//            } ?: "" + UUID.randomUUID()
//
//            q.insertInto(USERS)
//                .set(USERS.INSERTED_AT, requestTimestamp)
//                .set(USERS.UPDATED_AT, requestTimestamp)
//                .set(USERS.EMAIL, email.value)
//                .set(USERS.KIND, req.clientKind.name)
//                .set(USERS.LANG, req.lang.name)
//                .set(USERS.STATE, UserState.PROFILE_PENDING.name)
//                .set(USERS.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
//                .set(USERS.FIRST_NAME, firstName.value)
//                .set(USERS.LAST_NAME, lastName.value)
//                .execute()
//
//
//            val signInURL = "http://${clientDomain}${clientPortSuffix}/sign-in.html" // TODO:vgrechka Use HTTPS    a389374b-8132-44e9-a73b-fde56869d690
//
//            EmailMatumba.send(Email(
//                to = "${firstName.value} ${lastName.value} <${email.value}>",
//                subject = when (req.lang) {
//                    Language.UA -> when (req.clientKind) {
//                        ClientKind.CUSTOMER -> "Пароль для APS"
//                        ClientKind.WRITER -> "Пароль для Writer UA"
//                    }
//                    Language.EN -> when (req.clientKind) {
//                        ClientKind.CUSTOMER -> imf("Email title for en-customer")
//                        ClientKind.WRITER -> imf("Email title for en-writer")
//                    }
//                },
//                html = dedent(t(
//                    en = """
//                        TODO
//                    """,
//                    ua = """
//                        Привет, ${firstName.value}!<br><br>
//                        Вот твой пароль: ${password}
//                        <br><br>
//                        <a href="${signInURL}">${signInURL}</a>
//                    """
//                ))
//            ))
//        } catch (e: Throwable) {
//            throw e
////            if (e.code === '23505') {
////                fieldErrors.email = t('This email is already registered', 'Такая почта уже зарегистрирована')
////                return traceEndHandler(s{ret: fixErrorsResult()})
////            } else {
////                throw e
////            }
//        }
//    }
//
//}

