/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_SignUp_1 : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal() {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        assert_staticHomePage_rightNavbarSignIn()
        clog(1111)
    }

    override fun buildStepsAfterWorldBoot() {
        assert_staticHomePage_rightNavbarSignIn()
        clog(2222)
    }

}

