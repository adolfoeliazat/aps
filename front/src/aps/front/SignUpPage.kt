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

class SignUpPage(val world: World) {
    fun load(): Promisoid<Unit> = async {
        world.setPage(Page(
            header = oldShitAsToReactElementable(Shitus.pageHeader(json("title" to t("Sign Up", "Регистрация")))),
            body = oldShitAsToReactElementable(Shitus.diva(json(),
                                                           FormMatumba<SignUpRequest, GenericResponse>(FormSpec(
                                                               req = SignUpRequest(),
                                                               ui = world,
                                                               primaryButtonTitle = t("Proceed", "Вперед"),
                                                               onSuccessa = {async{
                        world.signedUpOK = true
                        world.initialEmailFieldValueAfterSignUp = req.immutableSignUpFields.email.value
                        world.pushNavigate("/sign-in.html")
                    }}
                )).toReactElement(),

                                                           Shitus.diva(json(),
                    hr(),
                    Shitus.diva(json("tame" to "signInSuggestion", "style" to json("textAlign" to "left")),
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
    }
}


