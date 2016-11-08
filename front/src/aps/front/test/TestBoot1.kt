/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front.test

import aps.*
import aps.front.*
import kotlin.browser.window

class TestBoot1 : TestScenario() {
    override fun run(): Promise<Unit> {"__async"
        __await(buildAndRunTestScenario {o->
            o.assert("Scenario should be tested against / path", window.location.pathname == "/")
            o.act {
                clog("Doing some stuff")
            }
            o.act("Doing nasty things") {
                clog("naaaaaaasty")
            }
        })
        return __asyncResult(Unit)
    }
}
