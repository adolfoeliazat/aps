/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front.test

import aps.*
import aps.front.*
import into.kommon.*
import kotlin.browser.*

class TestBoot3 : TestScenario() {
    override val shortDescription = "Valid token in local storage"

    lateinit var validToken: String

    override fun run0(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario {o->
            o.assert(window.location.pathname == "/", "Scenario should be tested against / path")

            o.acta("Sign Fucker up/in and approve his profile, bypassing UI") {"__async"
                __await(ImposeNextGeneratedPasswordRequest.send("fucker-secret"))

                run { // Sign-up
                    val res: FormResponse = __await(callMatumba(SignUpRequest()-{o->
                        o.agreeTerms.value = true
                        o.signUpFields.firstName.value = "Gaylord"
                        o.signUpFields.lastName.value = "Fucker"
                        o.signUpFields.email.value = "fucker@test.shit.ua"
                    }, null))

                    if (res is FormResponse.Shitty) {
                        console.warn("Fucker sign-up failed")
                        console.warn("---------------------")
                        console.warn(res.error)
                        res.fieldErrors.forEach {console.warn("* ${it.field}: ${it.error}")}
                        bitch("Fucker sign-up failed")
                    }
                }

                run { // Sign-in and obtain token
                    val res: FormResponse = __await(callMatumba(SignInWithPasswordRequest()-{o->
                        o.email.value = "fucker@test.shit.ua"
                        o.password.value = "fucker-secret"
                    }, null))

                    when (res) {
                        is FormResponse.Shitty -> {
                            console.warn("Fucker sign-in failed")
                            console.warn("---------------------")
                            console.warn(res.error)
                            res.fieldErrors.forEach {console.warn("* ${it.field}: ${it.error}")}
                            bitch("Fucker sign-in failed")
                        }
                        is FormResponse.Hunky<*> -> { // TODO:vgrechka A way to pass type args from parent sealed class to children
                            val meat = res.meat as SignInResponse
                            validToken = meat.token
                        }
                    }
                }

                __await(TestSetUserStateRequest.send("fucker@test.shit.ua", UserState.COOL))
                __asyncResult(Unit)
            }

            o.act {
                localStorage.clear()
                localStorage["token"] = validToken
                Globus.displayInitialShit()
            }
            o.state("There's some garbage token in localStorage, checking it")
            o.assertVisibleText("Дышите глубоко...")
            o.assertVisibleText_no("Приветствуем")
            o.assertVisibleText_no("Вход", under="#topNavbarContainer")

            o.acta {"__async"
                __reawait(World().boot())
            }
            o.state("Checked and rejected")
            o.assertVisibleText("Приветствуем")
            o.assertVisibleText_no("Дышите глубоко...")
            o.assertVisibleText("Gaylord", under="#topNavbarContainer")
        })
    }
}

