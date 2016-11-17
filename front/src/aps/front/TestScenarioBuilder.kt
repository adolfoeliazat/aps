/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.global
import jquery.jq

fun buildAndRunTestScenario(block: (TestScenarioBuilder) -> Unit): Promise<Unit> {"__async"
    val builder = TestScenarioBuilder()
    block(builder)
    return __reawait(builder.runScenario())
}

class TestScenarioBuilder {
    val instructions = mutableListOf<TestInstruction>()

    fun runScenario(): Promise<Unit> {"__async"
        return __reawait(art.run(instructions))
    }

    fun state(descr: String) {
        instructions.add(TestInstruction.Step.StateStep(descr))
    }

    fun assert(test: Boolean, descr: String) {
        val step = TestInstruction.Step.AssertionStep(descr)
        instructions.add(step)
        act {
            step.passed = test
            if (!step.passed) art.fail(descr)
        }
    }

    fun act(descr: String? = null, block: () -> Unit) {
        acta(descr, {"__async"
            block()
            __asyncResult(Unit)
        })
    }

    fun acta(descr: String? = null, block: () -> Promise<Unit>) {
        var step: TestInstruction.Step? = null
        if (descr != null) {
            step = TestInstruction.Step.ActionStep(descr)
            instructions.add(step)
        }

        instructions.add(TestInstruction.Do {"__async"
            __await(block())
            step?.passed = true
            __asyncResult(Unit)
        })
    }

    fun assertVisibleText(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should contain in $under: _${expected}_", {
            val actual = visibleText(under)
            actual.contains(expected)
        })
    }

    fun assertVisibleText_no(expected: String, under: CSSSelector = "body") {
        assertOnAnimationFrame("Page should not contain in $under: ~~_${expected}_~~", {
            val actual = visibleText(under)
            !actual.contains(expected)
        })
    }

    private fun visibleText(under: CSSSelector) = jq("$under *:not(:has(*)):visible").text()

    fun assertOnAnimationFrame(descr: String, test: () -> Boolean) {
        val step = TestInstruction.Step.AssertionStep(descr)
        instructions.add(step)
        acta {"__async"
            __await(tillAnimationFrame())
            step.passed = test()
            if (!step.passed) art.fail(descr)
            __asyncResult(Unit)
        }
    }
}

class prepareFucker(o: TestScenarioBuilder, userState: UserState) {
    lateinit var token: String
    init {
        o.acta("Sign Fucker up/in and approve his profile, bypassing UI") {"__async"
            __await(ImposeNextGeneratedPasswordRequest.send("fucker-secret"))

            __await(send(null, SignUpRequest() - { o ->
                o.agreeTerms.value = true
                o.signUpFields.firstName.value = "Gaylord"
                o.signUpFields.lastName.value = "Fucker"
                o.signUpFields.email.value = "fucker@test.shit.ua"
            })).orDie

            token = __await(sendSafe(null, SignInWithPasswordRequest() - { o ->
                o.email.value = "fucker@test.shit.ua"
                o.password.value = "fucker-secret"
            })).orDie.token

            __await(TestSetUserStateRequest.send("fucker@test.shit.ua", userState))
            __asyncResult(Unit)
        }
    }
}




