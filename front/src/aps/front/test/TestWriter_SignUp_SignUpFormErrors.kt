/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.front.WriterTestUtils.assert_staticHomePage_rightNavbarSignIn
import aps.front.WriterTestUtils.goto_signUpForm

class TestWriter_SignUp_SignUpFormErrors : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal(tsl: TypedStorageLocal) {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        assert_staticHomePage_rightNavbarSignIn(o)
    }

    override fun buildStepsAfterWorldBoot() {
        goto_signUpForm(o)

        o.clickDescribingStep("button-primary")
        o.assertRootHTMLExt("Bloody errors everywhere", "4d164f00-9537-4485-b5f9-3e6292760132")

        o.setValueDescribingStep("TextField-email.Input", "kafka@test.shit.ua")
        o.clickDescribingStep("button-primary")
        o.assertRootHTMLExt("-1 error", "e57a77cd-cd6b-4a1f-94b0-d53ce89704f0")

        o.setValueDescribingStep("TextField-firstName.Input", "Франц")
        o.clickDescribingStep("button-primary")
        o.assertRootHTMLExt("-1 error", "16afe805-022a-489f-b2ee-993acce1f5d6")

        o.setValueDescribingStep("TextField-lastName.Input", "Кафка")
        o.clickDescribingStep("button-primary")
        o.assertRootHTMLExt("-1 error", "8f9f82f8-ac33-4186-b6cf-1eea971d80b1")

        o.setValueDescribingStep("AgreeTermsField.Checkbox", true)
        o.clickDescribingStep("button-primary")
        o.assertRootHTMLExt("Success", "ee750a2a-45bb-4bed-80be-6c27d152ec89")
    }

}

