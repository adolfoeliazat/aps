/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.*

class TestBoot4 : TestScenario() {
    override val shortDescription = "Valid token in local storage, unapproved writer profile"

    override fun run0(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario{o->
            o.assert(window.location.pathname == "/", "Scenario should be tested against / path")

            val fucker = prepareFucker(o, UserState.PROFILE_APPROVAL_PENDING)

            o.act {
                localStorage.clear()
                localStorage["token"] = fucker.token
                ExternalGlobus.displayInitialShit()
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




















