/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_Boot2 : WriterBootTestScenario() {
    override val shortDescription = "Invalid token in local storage"

    override fun fillLocalStorage() {
        localStorage["token"] = "garbage"
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.state("There's some garbage token in localStorage, checking it")
        o.assertVisibleText("Дышите глубоко...")
        o.assertNoVisibleText("Приветствуем")
        o.assertNoVisibleText("Вход", under="#topNavbarContainer")
    }

    override fun buildStepsAfterWorldBoot() {
        o.state("Checked and rejected")
        o.assertVisibleText("Приветствуем")
        o.assertNoVisibleText("Дышите глубоко...")
        o.assertVisibleText("Вход", under="#topNavbarContainer")
    }

}

