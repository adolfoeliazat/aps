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
import aps.front.testutils.*
import into.kommon.*
import kotlin.browser.*

class TestWriter_SignUp_HappyPath : WriterBootTestScenario() {
    lateinit var adminWorld: World
    lateinit var initialWorldURL: String

    override val path = "/"

    override fun fillStorageLocal(tsl: TypedStorageLocal) {
    }

    override fun buildStepsAfterDisplayInitialShit() {
        o.act {putTinyTestContextLabel("Writer: Kafka")}
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

            o.assertEmailThenClear(
                "Received password",
                "qwe",
                "Пароль для Writer UA",
                """
                    Привет, Франц!<br><br>
                    Вот твой пароль: secret-big-as-fuck
                    <br><br>
                    <a href="http://aps-ua-writer.local:3022/sign-in.html">http://aps-ua-writer.local:3022/sign-in.html</a>
                """)
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
                o.act {
                    initialWorldURL = Globus.realLocation.href
                    initialWorld.unmountShit()
                    putTinyTestContextLabel("Admin: Dasja")
                }
                initNewBrowser(o, fillStorageLocal = {})
                pushWriterURL(o, "/dashboard.html")
                bootWorld(o, "admin") {adminWorld = it}
            }
            o.assertRootHTMLExt("Sign-in page", "158c6166-2466-409c-8f24-746e30e35a6f")

            o.setValueDescribingStep("TextField-email.Input", "dasja@test.shit.ua")
            o.setValueDescribingStep("TextField-password.Input", "secret")
            o.clickDescribingStep("button-primary")
            o.assertRootHTMLExt("Dashboard with one profile approval task", "f5f0b82d-ddc1-481f-9cbc-d53b8f9a64bf")

            o.clickDescribingStep("section-workPending.profilesToApprove.link")
            o.assertRootHTMLExt("One profile is waiting for approval", "c2ddb086-ef22-4d40-b1f8-781de197f411")
            o.clickDescribingStep("chunk-i000.item-i000.heading.icon-edit")
            o.assertRootHTMLExt("User editing form", "1d0cf419-658e-4753-bbc8-ad54f80d8d69")
            o.setValueDescribingStep("chunk-i000.item-i000.SelectField-state.Select", "COOL")
            o.setValueDescribingStep("chunk-i000.item-i000.TextField-adminNotes.Input", "Ладно, примем засранца")
            o.clickDescribingStep("chunk-i000.item-i000.button-primary")
            o.assertRootHTMLExt("User is updated", "cfe6d637-4ca6-42ee-9fad-1fc249a328ee")

            o.assertEmailThenClear(
                "Notification is sent to writer",
                "qwe",
                "Тебя пустили на Writer UA",
                """
                    Привет, Франц!<br><br>
                    Тебя пустили на сайт, заходи и пользуйся. Только не шали.
                    <br><br>
                    <a href="http://aps-ua-writer.local:3022/dashboard.html">http://aps-ua-writer.local:3022/dashboard.html</a>
                """)
        }

        o.section("Writer is able to use site") {
            o.actionStep("Writer opens link from email") {
                o.act {
                    adminWorld.unmountShit()
                    putTinyTestContextLabel("Writer: Kafka #2")
                    Globus.location.pushState(null, "", "http://aps-ua-writer.local:3022/profile.html")
                }
            }
            initNewBrowser(o, fillStorageLocal = {it.token = initialStorageLocal.token})
            bootWorld(o, "writer2"){}
            o.assertRootHTMLExt("Everything is fucking cool", "840a206b-5676-4cac-9f84-d712e7e1033c")
        }
    }

}



