/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import kotlin.browser.window
import kotlin.js.json

class SignUpPage {
    private val world = Globus.world

    suspend fun load(): PageLoadingError? {
        world.setPage(Page(
            header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("Sign Up", "Регистрация")))),
            body = oldShitAsToReactElementable(Shitus.diva(
                json(),
                FormMatumba<SignUpRequest, GenericResponse>(FormSpec(
                    req = SignUpRequest(),
                    primaryButtonTitle = t("Proceed", "Вперед"),
                    onSuccessa = {
                        world.signedUpOK = true
                        world.initialEmailFieldValueAfterSignUp = req.email.value
                        world.pushNavigate(makeURL(when (Globus.clientKind) {
                                                       ClientKind.UA_CUSTOMER -> pages.uaCustomer.signIn
                                                       ClientKind.UA_WRITER -> pages.uaWriter.signIn
                                                   }, listOf()))
}
                )).toReactElement(),

                Shitus.diva(
                    json(),
                    hr(),
                    Shitus.diva(
                        json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
                        Shitus.spanc(json("tame" to "prose", "content" to t("TOTE", "Уже есть аккаунт? Тогда "))),
                        urlLink(linkParams = LinkParams(title = t("TOTE", "входим сюда")), url = "sign-in.html", key = links.signUp).toReactElement(),
                        "."))
            ))
        ))

//        world.footer.setBurgerMenu(Menu(listOf(
//            MenuItem("Иво Бобул") {async{
//                Input.instance("email").setValue("apsua-ivo.bobul@mailinator.com")
//                Input.instance("firstName").setValue("Иво")
//                Input.instance("lastName").setValue("Бобул")
//                Checkbox.instance("agreeTerms").setValue(true)
//                await(Button.instance("primary").click())
//            }}
//        )))

        return null
    }
}


