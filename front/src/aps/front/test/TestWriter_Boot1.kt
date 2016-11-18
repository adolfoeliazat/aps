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

    override fun fillLocalStorage() {}

    override fun buildStepsAfterWorldBoot() {
        o.state("JS arrived, nothing changed visually")
        o.assertVisibleText("Приветствуем")
        o.assertVisibleText("Вход", under = "#topNavbarContainer")
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("Showing static content")
        o.assertVisibleText("Приветствуем")
        o.assertVisibleText("Вход", under = "#topNavbarContainer")
    }
}

