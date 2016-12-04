/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.*
import aps.front.TestUtils.bootWorld
import aps.front.TestUtils.initNewBrowser
import aps.front.TestUtils.pushWriterURL
import aps.front.TestUtils.putTinyTestContextLabel
import aps.front.WriterTestUtils.assert_staticHomePage_rightNavbarSignIn
import aps.front.WriterTestUtils.goto_signUpForm
import kotlin.browser.*

class TestWriter_SignUp_HappyPath : WriterBootTestScenario() {
    override val path = "/"

    override fun fillStorageLocal(tsl: TypedStorageLocal) {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        putTinyTestContextLabel(o, "Writer: Kafka")
        assert_staticHomePage_rightNavbarSignIn(o)
    }

    override fun buildStepsAfterWorldBoot() {
        goto_signUpForm(o)

        o.section("Writer signs up, then fills in profile and sends it for review") {
            o.setValueDescribingStep("TextField-email.Input", "kafka@test.shit.ua")
            o.setValueDescribingStep("TextField-firstName.Input", "Франц")
            o.setValueDescribingStep("TextField-lastName.Input", "Кафка")
            o.setValueDescribingStep("AgreeTermsField.Checkbox", true)
            o.acta {ClearSentEmailsRequest.send()}
            o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:24:32")}
            o.acta {ImposeNextGeneratedPasswordRequest.send("secret-big-as-fuck")}
            o.clickDescribingStep("button-primary")

            o.acta {debugCheckEmail()}
            o.assertMail("Received password", """
                Привет, Франц!<br><br>
                Вот твой пароль: secret-big-as-fuck
                <br><br>
                <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>
            """)
            o.act {debugHideMailbox()}
            o.assertRootHTMLExt("Success message and sign in form", "6f01bc45-10c3-4c5f-9801-10d2f3fe6ebc")

            o.setValueDescribingStep("TextField-password.Input", "secret-big-as-fuck")
            o.click("button-primary")

            o.assertRootHTMLExt("Request to fill in profile", "5d5e9db5-4ce1-4b83-ac7b-5ff6f5cc0976")
            o.setValueDescribingStep("TextField-phone.Input", "1234567")
            o.setValueDescribingStep("TextField-aboutMe.Input", "Пишу всякую хуйню за деньги")
            o.acta {ImposeNextRequestTimestampRequest.send("2016-12-02 12:28:32")}
            o.clickDescribingStep("button-primary")

            o.assertRootHTMLExt("Profile with message about admin checking your shit", "7aeb1592-1d80-4fbf-9b88-b439da9496d3")
        }

        o.section("Admin checks shit") {
            o.actionStep("Admin opens browser") {
                putTinyTestContextLabel(o, "Admin: Dasja")
                initNewBrowser(o, fillStorageLocal = {fillStorageLocal(it)})
                pushWriterURL(o, "/dashboard.html")
                bootWorld(o)
            }
            o.assertRootHTMLExt("Sign-in page", "158c6166-2466-409c-8f24-746e30e35a6f")

            o.setValueDescribingStep("TextField-email.Input", "dasja@test.shit.ua")
            o.setValueDescribingStep("TextField-password.Input", "secret")
            o.clickDescribingStep("button-primary")
            o.assertRootHTMLExt("Dashboard with one profile approval task", "f5f0b82d-ddc1-481f-9cbc-d53b8f9a64bf")
        }
    }

}


