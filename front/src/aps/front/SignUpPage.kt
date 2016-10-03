/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class SignUpPage(val ui: LegacyUIShit) {
    fun load(): Promise<Unit> {"__async"
        ui.setPage(json(
            "header" to jshit.pageHeader(json("title" to t("Sign Up", "Регистрация"))),
            "body" to jshit.diva(json(),
                FormMatumba(SignUpRequest(), GenericResponse(), ui=ui) {
                    primaryButtonTitle = t("Proceed", "Вперед")

                    onSuccessa = {res -> "__async"
                        ui.signedUpOK = true
                        __await(ui.pushNavigate("sign-in.html")) /ignora
                    }
                }.toReactElement(),

                jshit.diva(json(),
                    jshit.hr(),
                    jshit.diva(json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
                        jshit.spanc(json("tame" to "prose", "content" to t("TOTE", "Уже есть аккаунт? Тогда "))),
                        ui.urlLink(json("tamy" to true, "title" to t("TOTE", "входим сюда"), "url" to "sign-in.html", "delayActionForFanciness" to true)),
                        "."))
                )
        ))

        return __asyncResult(Unit)
    }
}


