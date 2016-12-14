/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import kotlin.browser.window

class SignUpPage(val world: World) {
    fun load(): Promise<Unit> = async {
        world.setPage(Page(
            header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("Sign Up", "Регистрация")))),
            body = oldShitAsToReactElementable(Shitus.diva(json(),
                                                           FormMatumba<SignUpRequest, GenericResponse>(FormSpec(
                                                               SignUpRequest(),
                                                               world,
                                                               primaryButtonTitle = t("Proceed", "Вперед"),
                                                               onSuccessa = {async{
                        world.signedUpOK = true
                        world.initialEmailFieldValueAfterSignUp = req.immutableSignUpFields.email.value
                        await(world.pushNavigate("sign-in.html"))
                    }}
                )).toReactElement(),

                                                           Shitus.diva(json(),
                    hr(),
                    Shitus.diva(json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
                                Shitus.spanc(json("tame" to "prose", "content" to t("TOTE", "Уже есть аккаунт? Тогда "))),
                                world.urlLink(json("tamy" to true, "title" to t("TOTE", "входим сюда"), "url" to "sign-in.html", "delayActionForFanciness" to true)),
                                "."))
                ))
        ))

        world.footer.setBurgerMenu(Menu(listOf(
            MenuItem("Иво Бобул") {async{
                Input.instance("email").setValue("ivo.bobul@mailinator.com")
                Input.instance("firstName").setValue("Иво")
                Input.instance("lastName").setValue("Бобул")
                Checkbox.instance("agreeTerms").setValue(true)
                Button.instance("primary").click()
            }}
        )))
    }
}


