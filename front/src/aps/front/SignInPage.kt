/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import java.util.*

fun jsFacing_loadSignInPageCtor(ui: LegacyUIShit, ui2: UI2): dynamic {
    fun loadSignInPage() {
        ui2.setPage(
            header = pageHeader(t("Sign In", "Вход")),
            body = kdiv{o->
                o-nif(ui.signedUpOK) {preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.")))}

                o-FormMatumba<SignInWithPasswordRequest, SignInWithPasswordRequest.Response>(FormSpec(
                    SignInWithPasswordRequest(),
                    ui,
                    primaryButtonTitle = t("Sign In", "Войти"),
                    onError = {
                        spec.req.password.value = ""
                    },

                    onSuccessa = {res -> "__async"
                        ui.user = res.user
                        ui.startLiveStatusPolling()
                        ui.token = res.token
                        hrss.storageLocal.setItem("token", ui.token)

                        __await(ui.pushNavigate(when (res.user.state) {
                            UserState.COOL -> "dashboard.html"
                            else -> "profile.html"
                        })) /ignora
                    }
                ))

                o-nif(!ui.signedUpOK) {kdiv{o->
                    o-hr()
                    o-kdiv(Style(textAlign="left")) {o->
                        o-t("TOTE", "Как? Еще нет аккаунта? ")
                        o-ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))
                    }
                }}
            }
        )
    }

    return ::loadSignInPage
}




