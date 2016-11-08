/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front.test

import into.kommon.*
import aps.*
import aps.front.*

class TestBoot1 : TestScenario() {
    override fun run(): Promise<Unit> {"__async"
        return __await(art.run(listOf(
            TestInstruction.Step.Action("Do this"),
            TestInstruction.Do {"__async"; die(); __asyncResult(Unit)},
            TestInstruction.Step.Action("Do that")
        ))) /ignora

//        return __await(art.run(listOf(
//            TestInstruction.Do {"__async"
//                println("aaaaaaaaaaaaaaaa")
//                __asyncResult(Unit)
//            }
//        ))) /ignora
    }
}
