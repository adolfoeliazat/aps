/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_Boot_InvalidToken : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal() {
        typedStorageLocal.token = "garbage"
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("There's some garbage token in localStorage, checking it")
        assert_breatheBanner_rightNavbarEmpty()
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Checked and rejected")
        assert_staticHomePage_rightNavbarSignIn()
    }

}

