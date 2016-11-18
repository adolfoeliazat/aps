package aps.front

import aps.*

abstract class WriterBootTestScenario : BootTestScenario() {
    override val clientKind = ClientKind.WRITER
    lateinit var fuckerToken: String

    fun prepareFucker(userState: UserState): Promise<Unit> {"__async"
        __await(ImposeNextGeneratedPasswordRequest.send("fucker-secret"))

        __await(send(null, SignUpRequest() - { o ->
            o.agreeTerms.value = true
            o.signUpFields.firstName.value = "Gaylord"
            o.signUpFields.lastName.value = "Fucker"
            o.signUpFields.email.value = "fucker@test.shit.ua"
        })).orDie

        fuckerToken = __await(sendSafe(null, SignInWithPasswordRequest() - { o ->
            o.email.value = "fucker@test.shit.ua"
            o.password.value = "fucker-secret"
        })).orDie.token

        __await(TestSetUserStateRequest.send("fucker@test.shit.ua", userState))
        return __asyncResult(Unit)
    }

}

