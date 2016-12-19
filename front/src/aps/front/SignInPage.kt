/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class SignInPage(val ui: World) {

    fun load(): Promise<Unit> = async {
        ui.setPage(Page(
            header = oldShitAsToReactElementable(pageHeader(t("Sign In", "Вход"))),
            body = kdiv{o->
                o-nif(ui.signedUpOK) {preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом.")))}

                val form = FormMatumba<SignInWithPasswordRequest, SignInResponse>(FormSpec(
                    SignInWithPasswordRequest(),
                    ui,
                    primaryButtonTitle = t("Sign In", "Войти"),
                    onError = {
                        spec.req.password.value = ""
                    },

                    onSuccessa = {res ->
                        async {
                            ui.maybeUser = res.user
//                        ui.startLiveStatusPolling()
                            ui.token = res.token
                            typedStorageLocal.token = ui.token
//                        hrss.storageLocal.setItem("token", token!!)

                            await(ui.pushNavigate(when (res.user.state) {
                                                   UserState.COOL -> "dashboard.html"
                                                   else -> "profile.html"
                                               }))
                        }
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
                        o-urlLink(tamyShamy="createAccount",
                                  title=t("TOTE", "Срочно создать!"),
                                  url="sign-up.html",
                                  delayActionForFanciness=true)

//                        o-ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))
                    }
                }}
            }
        ))
    }

}





