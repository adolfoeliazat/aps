/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import kotlin.browser.*

class TestWriter_SignUp_HappyPath : WriterBootTestScenario(), WriterTestUtils {
    override val path = "/"

    override fun fillStorageLocal() {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        assert_staticHomePage_rightNavbarSignIn(o)
    }

    override fun buildStepsAfterWorldBoot() {
        goto_signUpForm(o)

        o.setValue("TextField-email.Input", "kafka@test.shit.ua")
        o.setValue("TextField-firstName.Input", "Франц")
        o.setValue("TextField-lastName.Input", "Кафка")
        o.setValue("AgreeTermsField.Checkbox", true)
        o.acta {ClearSentEmailsRequest.send()}
        o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:24:32")}
        o.acta {ImposeNextGeneratedPasswordRequest.send("secret-big-as-fuck")}
        o.click("button-primary")

        o.acta {debugCheckEmail()}
        o.assertMail("Received password", """
            Привет, Франц!<br><br>
            Вот твой пароль: secret-big-as-fuck
            <br><br>
            <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>
        """)
        o.act {debugHideMailbox()}
        o.assertRootHTMLExt("Success message and sign in form", "6f01bc45-10c3-4c5f-9801-10d2f3fe6ebc")

        o.setValue("TextField-password.Input", "secret-big-as-fuck")
        o.click("button-primary")

        o.assertRootHTMLExt("Request to fill in profile", "5d5e9db5-4ce1-4b83-ac7b-5ff6f5cc0976")
        o.setValue("TextField-phone.Input", "1234567")
        o.setValue("TextField-aboutMe.Input", "Пишу всякую хуйню за деньги")
        o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:28:32")}
        o.click("button-primary")

        o.assertRootHTMLExt("Profile with message about admin checking your shit", "7aeb1592-1d80-4fbf-9b88-b439da9496d3")
    }

}

