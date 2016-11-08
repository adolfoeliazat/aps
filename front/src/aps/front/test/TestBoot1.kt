/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front.test

import aps.*
import aps.front.*
import kotlin.browser.*

class TestBoot1 : TestScenario() {
    override fun run(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario {o->
            o.assert(window.location.pathname == "/", "Scenario should be tested against / path")
            o.act {
                localStorage.clear()
                localStorage["token"] = "garbage"
                Globus.displayInitialShit()
            }
            o.assertVisibleText("Дышите глубоко...")
        })
    }
}

