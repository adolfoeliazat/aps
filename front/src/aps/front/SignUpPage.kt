/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class SignUpPage(val ui: World) {
    fun load(): Promise<Unit> {"__async"
        ui.setPage(Page(
            header = oldShitAsReactElementable(Shitus.pageHeader(json("title" to t("Sign Up", "Регистрация")))),
            body = oldShitAsReactElementable(Shitus.diva(json(),
                FormMatumba<SignUpRequest, GenericResponse>(FormSpec(
                    SignUpRequest(),
                    ui,
                    primaryButtonTitle = t("Proceed", "Вперед"),
                    onSuccessa = {"__async"
                        ui.signedUpOK = true
                        __await(ui.pushNavigate("sign-in.html")) /ignora
                    }
                )).toReactElement(),

                Shitus.diva(json(),
                    hr(),
                    Shitus.diva(json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
                        Shitus.spanc(json("tame" to "prose", "content" to t("TOTE", "Уже есть аккаунт? Тогда "))),
                        ui.urlLink(json("tamy" to true, "title" to t("TOTE", "входим сюда"), "url" to "sign-in.html", "delayActionForFanciness" to true)),
                        "."))
                ))
        ))

        return __asyncResult(Unit)
    }
}


