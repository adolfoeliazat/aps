/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.*

class TestWriter_Boot_Cool : WriterBootTestScenario_FuckerToken() {
    override val shortDescription = "Valid token in local storage, user is cool"
    override val path = "/"

    override fun setFuckerFields(o: TestSetUserFieldsRequest) {
        o.state.value = UserState.COOL
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Fucker is signed in")
        assert_staticHomePage_rightNavbarGaylord()
    }
}

// TODO:vgrechka Should be generated
fun send(token: String?, req: SignUpRequest): Promise<FormResponse2<GenericResponse>> {"__async"
    return __asyncResult(__await(_send(token, req)))
}
// TODO:vgrechka Should be generated
fun sendSafe(token: String?, req: SignUpRequest): Promise<FormResponse2<GenericResponse>> {"__async"
    return __asyncResult(__await(_sendSafe(token, req)))
}
// TODO:vgrechka Should be generated
fun send(token: String?, req: SignInWithPasswordRequest): Promise<FormResponse2<SignInResponse>> {"__async"
    return __asyncResult(__await(_send(token, req)))
}
// TODO:vgrechka Should be generated
fun sendSafe(token: String?, req: SignInWithPasswordRequest): Promise<FormResponse2<SignInResponse>> {"__async"
    return __asyncResult(__await(_sendSafe(token, req)))
}

fun <Req: RequestMatumba, Meat> _send(token: String?, req: Req): Promise<FormResponse2<Meat>> {"__async"
    Globus.lastAttemptedRPCName = ctorName(req)
    val res: FormResponse = __await(callMatumba(req, token))
    return __asyncResult(when (res) {
        is FormResponse.Shitty -> {
            FormResponse2.Shitty(res.error, res.fieldErrors)
        }
        is FormResponse.Hunky<*> -> {
            FormResponse2.Hunky(res.meat as Meat)
        }
    })
}

fun <Req: RequestMatumba, Meat> _sendSafe(token: String?, req: Req): Promise<FormResponse2<Meat>> {"__async"
    return __asyncResult(
        try {
            __await(_send<Req, Meat>(token, req))
        } catch(e: Throwable) {
            FormResponse2.Shitty<Meat>(t("Service is temporarily fucked up, sorry", "Сервис временно в жопе, просим прощения"), listOf())
        })
}

fun <Meat, T> FormResponse2<Meat>.switch(hunky: (meat: Meat) -> T,
                                         shitty: (error: FormResponse2.Shitty<Meat>) -> T)
    : T = when (this) {
    is FormResponse2.Shitty -> shitty(this)
    is FormResponse2.Hunky -> hunky(this.meat)
}

fun <Meat> FormResponse2<Meat>.whenShitty(shitty: (error: FormResponse2.Shitty<Meat>) -> Unit)
    : Unit = this.switch(hunky = {it},
                         shitty = {shitty(it)})

val <Meat> FormResponse2<Meat>.orDie: Meat get() = this.switch(
    hunky = {it},
    shitty = {
        throw FatException("Got shitty response from ${Globus.lastAttemptedRPCName}: ${it.error}",
                           markdownPayload =
                               if (it.fieldErrors.isEmpty())
                                   "No field errors"
                               else
                                   "Field errors:\n" +
                                   it.fieldErrors.map{"* ${it.field}: ${it.error}"}.joinToString("\n"))
    }
)




















