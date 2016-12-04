/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import aps.front.WriterTestUtils.assert_staticHomePage_rightNavbarSignIn
import kotlin.browser.*

class TestWriter_Boot_NoToken : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal(tsl: TypedStorageLocal) {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("Showing static content")
        assert_staticHomePage_rightNavbarSignIn(o)
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("JS arrived, nothing changed visually")
        assert_staticHomePage_rightNavbarSignIn(o)
    }

}

