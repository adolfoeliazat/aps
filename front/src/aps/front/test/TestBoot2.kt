/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front.test

import aps.*
import aps.front.*
import kotlin.browser.*

class TestBoot2 : TestScenario() {
    override val shortDescription = "Invalid token in local storage"

    override fun run0(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario {o->
            o.assert(window.location.pathname == "/", "Scenario should be tested against / path")
            o.act {
                localStorage.clear()
                localStorage["token"] = "garbage"
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
            o.assertVisibleText("Вход", under="#topNavbarContainer")
        })
    }
}

