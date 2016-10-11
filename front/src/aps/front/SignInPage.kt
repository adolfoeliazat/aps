package aps.front

import aps.*
import java.util.*

fun jsFacing_loadSignInPageCtor(ui: LegacyUIShit): dynamic {
    fun loadSignInPage() {

        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Sign In", "Вход"))),
            "body" to jshit.diva(json(),
                ui.signedUpOK && jshit.preludeWithGreenCheck(json("title" to t(
                    "Cool. You have an account now. We sent you email with password.",
                    "Все круто. Теперь у тебя есть аккаунт. Пароль мы отправили письмом."))),

                FormMatumba<SignInWithPasswordRequest, SignInWithPasswordRequest.Response>(FormSpec(
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
                        jshit.storageLocal.setItem("token", ui.token)

                        __await(ui.pushNavigate(when (res.user.state) {
                            UserState.COOL -> "dashboard.html"
                            else -> "profile.html"
                        })) /ignora
                    }
                )).toReactElement(),

                !ui.signedUpOK && jshit.diva(json(),
                    jshit.hr(),
                    jshit.diva(json("style" to json("textAlign" to "left")),
                        t("TOTE", "Как? Еще нет аккаунта? "),
                        ui.urlLink(json("tamyShamy" to "createAccount", "title" to t("TOTE", "Срочно создать!"), "url" to "sign-up.html", "delayActionForFanciness" to true))))
            )
        ))
    }

    return ::loadSignInPage
}




