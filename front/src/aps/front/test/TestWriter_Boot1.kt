/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_Boot1 : WriterBootTestScenario() {
    override val shortDescription = "No token in local storage"

    override fun fillLocalStorage() {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("Showing static content")
        assert_staticHomePage_rightNavbarSignIn()
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("JS arrived, nothing changed visually")
        assert_staticHomePage_rightNavbarSignIn()
    }

}

