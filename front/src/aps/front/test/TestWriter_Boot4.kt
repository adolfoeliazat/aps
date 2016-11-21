/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import kotlin.browser.*

class TestWriter_Boot4 : WriterBootTestScenario() {
    override val shortDescription = "Valid token in local storage, profile is not filled"

    override fun prepareShit(): Promise<Unit> {"__async"
        return __reawait(prepareFucker(UserState.PROFILE_PENDING))
    }

    override fun fillStorageLocal() {
        typedStorageLocal.token = fuckerToken
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Checked and rejected")
        o.assertVisibleText("Приветствуем")
        o.assertNoVisibleText("Дышите глубоко...")
        o.assertVisibleText("Gaylord", under="#topNavbarContainer")
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("There's some garbage token in localStorage, checking it")
        o.assertVisibleText("Дышите глубоко...")
        o.assertNoVisibleText("Приветствуем")
        o.assertNoVisibleText("Вход", under="#topNavbarContainer")
    }

}




















