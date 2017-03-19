/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.back

import aps.*

@Servant class ServeSignUp : BitchyProcedure() {
    override fun serve() {
        // TODO:vgrechka Peculiarly log wrong-password sign-in attempts
        // TODO:vgrechka Prevent things like writer signing into customer-facing site
        // TODO:vgrechka Store tokens in Redis instead of DB

        fuckAnonymous(FuckAnonymousParams(
            bpc = bpc, makeRequest = {SignUpRequest()},
            runShit = fun(ctx, req: SignUpRequest): SignUpRequest.Response {
                val password = generatePassword()
                val kind = when (ctx.clientKind) {
                    ClientKind.UA_CUSTOMER -> UserKind.CUSTOMER
                    ClientKind.UA_WRITER -> UserKind.WRITER
                }
                val user = saveUserToRepo(User(
                    user = UserFields(
                        firstName = req.firstName.value,
                        email = req.email.value,
                        lastName = req.lastName.value,
                        passwordHash = hashPassword(password),
                        profilePhone = "",
                        kind = kind,
                        state = when (ctx.clientKind) {
                            ClientKind.UA_CUSTOMER -> imf("477250b3-8fde-4881-8794-888d76674fef")
                            ClientKind.UA_WRITER -> UserState.PROFILE_PENDING
                        },
                        adminNotes = "",
                        subscribedToAllCategories = when (kind) {
                            UserKind.CUSTOMER -> false // Dummy
                            UserKind.WRITER -> true
                            UserKind.ADMIN -> wtf("c876fcc0-5c37-4ef4-bb11-3c8da75e616c")
                        }
                    )
                ))

                val signInURL = ctx.clientRoot + "/signIn.html"
                val productName = when (ctx.clientKind) {
                    ClientKind.UA_CUSTOMER -> const.productName.uaCustomer
                    ClientKind.UA_WRITER -> const.productName.uaWriter
                }
                EmailMatumba.send(Email(
                    to = "${user.user.firstName} ${user.user.lastName} <${user.user.email}>",
                    subject = "[$productName] Пароль",
                    html = dedent(t(
                        en = """TOTE""",
                        ua = """
                            <div style='font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;'>
                                <div style='padding-bottom: 1em;'>Привет, ${escapeHTML(user.user.firstName)}!</div>
                                <div>Вот твой пароль для <a href="$signInURL">входа</a> в $productName: $password</div>
                                <div style='padding-top: 2em; font-style: italic;'>$productName</div>
                            </div>
                        """
                    ))
                ))
                return SignUpRequest.Response()
            }
        ))
    }
}









//@RemoteProcedureFactory fun serveSignUp() = publicProcedure(
//    {SignUpRequest()},
//    runShit = fun(ctx, req): SignUpRequest.Response {
//        try {
//            val firstName = req.mutableSignUpFields.firstName.value
//            val lastName = req.mutableSignUpFields.lastName.value
//            val email = req.immutableSignUpFields.email.value
//            val password = TestServerFiddling.nextGeneratedPassword.getAndReset()
//                ?: UUID.randomUUID().toString()
//
//            val userID = USERS.let {t->
//                ctx.insertShit("Insert user", t) {it
//                    .set(t.INSERTED_AT, RequestGlobus.stamp)
//                    .set(t.UPDATED_AT, RequestGlobus.stamp)
//                    .set(t.EMAIL, email)
//                    .set(t.KIND, when (ctx.clientKind) {
//                        ClientKind.UA_CUSTOMER -> JQUserKind.CUSTOMER
//                        ClientKind.UA_WRITER -> JQUserKind.WRITER
//                    })
//                    .set(t.LANG, ctx.lang.name)
//                    .set(t.STATE, when(ctx.clientKind) {
//                        ClientKind.UA_CUSTOMER -> UserState.COOL.name
//                        ClientKind.UA_WRITER -> UserState.PROFILE_PENDING.name
//                    })
//                    .set(t.PASSWORD_HASH, BCrypt.hashpw(password, BCrypt.gensalt()))
//                    .set(t.FIRST_NAME, firstName)
//                    .set(t.LAST_NAME, lastName)
//                    .set(t.ADMIN_NOTES, "")
//                    .returnID(t)
//                }
//            }
//
//            val signInURL = "${RequestGlobus.commonRequestFields.clientURL}/sign-in.html"
//
//            EmailMatumba.send(Email(
//                to = "$firstName $lastName <$email>",
//                subject = when (ctx.clientKind) {
//                    ClientKind.UA_CUSTOMER -> "Пароль для APS"
//                    ClientKind.UA_WRITER -> "Пароль для Writer UA"
//                },
////                subject = when (ctx.lang) {
////                    Language.UA -> when (ctx.clientKind) {
////                        ClientKind.CUSTOMER -> "Пароль для APS"
////                        ClientKind.WRITER -> "Пароль для Writer UA"
////                    }
////                    Language.EN -> when (ctx.clientKind) {
////                        ClientKind.CUSTOMER -> imf("Email title for en-customer")
////                        ClientKind.WRITER -> imf("Email title for en-writer")
////                    }
////                },
//                html = dedent(t(
//                    en = """
//                        TODO
//                    """,
//                    ua = """
//                        Привет, $firstName!<br><br>
//                        Вот твой пароль: ${password}
//                        <br><br>
//                        <a href="${signInURL}">${signInURL}</a>
//                    """
//                ))
//            ))
//            return SignUpRequest.Response(userID.toString())
//        } catch (e: Throwable) {
//            if (e is DataAccessException) {
//                val sqlState = e.sqlState()
//                dlog("SQL state: ", sqlState)
//                if (sqlState == "23505")
//                    bitchExpectedly(t("This email is already registered", "Эта почта уже занята"))
//            }
//            throw e
//        }
//    },
//
//    validate = {ctx, req ->
//        if (req.agreeTerms.no) {
//            ctx.fieldErrors.add(FieldError(req.agreeTerms.name, t("You have to agree with terms and conditions", "Необходимо принять соглашение")))
//        }
//    }
//)


