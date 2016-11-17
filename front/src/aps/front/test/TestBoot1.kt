/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestBoot1 : TestScenario() {
    override val shortDescription = "No token in local storage"

    override fun run0(): Promise<Unit> {"__async"
        return __reawait(buildAndRunTestScenario {o->
            o.assert(window.location.pathname == "/", "Scenario should be tested against / path")
            o.act {
                localStorage.clear()
                ExternalGlobus.displayInitialShit()
            }
            o.state("Showing static content")
            o.assertVisibleText("Приветствуем")
            o.assertVisibleText("Вход", under="#topNavbarContainer")

            o.acta {"__async"
                __reawait(World().boot())
            }
            o.state("JS arrived, nothing changed visually")
            o.assertVisibleText("Приветствуем")
            o.assertVisibleText("Вход", under="#topNavbarContainer")
        })
    }
}

