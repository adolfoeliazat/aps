/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.js.json

class SignInPage(val ui: World) {

    suspend fun load(): PageLoadingError? {
        ui.setPage(Page(
            header = oldShitAsToReactElementable(pageHeader(t("Sign In", "Вход"))),
            body = kdiv{o->
                o-nif(ui.signedUpOK) {preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.")))}

                val form = FormMatumba<SignInWithPasswordRequest, SignInResponse>(FormSpec(
                    req = SignInWithPasswordRequest(),
                    ui = ui,
                    primaryButtonTitle = t("Sign In", "Войти"),
                    onError = {
                        spec.req.password.value = ""
                    },

                    onSuccessa = {res ->
                        ui.userMaybe = res.user
                        ui.tokenMaybe = res.token
                        Globus.currentBrowseroid.typedStorageLocal.token = ui.tokenMaybe

                        ui.pushNavigate(
                            when (res.user.state) {
                                UserState.COOL -> "dashboard.html"
                                else -> "profile.html"
                            })
                    }
                ))

                if (ui.signedUpOK) {
                    form.req.email.value = ui.initialEmailFieldValueAfterSignUp
                }

                o- form

                o-nif(!ui.signedUpOK) {kdiv{o->
                    o-hr()
                    o-kdiv(textAlign="left"){o->
                        o-t("TOTE", "Как? Еще нет аккаунта? ")
                        o-urlLink(
                            linkParams = LinkParams(title=t("TOTE", "Срочно создать!")),
                            url = makeURL(pages.uaWriter.signUp, listOf()),
                            delayActionForFanciness = true,
                            key = links.createAccount
                        )
                    }
                }}
            }
        ))
        return null
    }
}





