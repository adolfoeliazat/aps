package aps.front

import aps.*
import aps.front.testutils.*

class TestUACustomer_Order_Files_Edit : StepBasedTestScenario() {
    val shit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate_1(
            shit,
            setUpUsers = {
                o.setUpBobul(shit)
                o.setUpFedor(shit)
            },
            setUpOrders = {
                o.setUpBobulOrder(
                    shit,
                    setUpFiles = {oid->
                        async {
                            await(setUpFilesByBobul_1(shit, oid))
                            await(setUpFilesByFedor_1(shit, oid))
                            await(setUpFilesByBobul_2(shit, oid))
                            await(setUpFilesByFedor_2(shit, oid))
                        }
                    })
            },
            assertScreen = {o.assertScreenHTML("1", "ecec02be-35ab-4c44-afc4-a50b73d45b1c")}
        )

        o.act {jqbody.scrollTop(700)}
        o.kicClick("edit-0-3")
        o.assertScreenHTML_todo("Piece of shit 0-3 is opened for editing", "31c57c9d-06e2-43ee-beef-106f1689b954")

        o.section("Edit some shit without changing file") {
            o.act {jqbody.scrollTop(900)}
            o.kicClick("edit-0-5")
            o.assertScreenHTML_todo("Piece of shit 0-5 is opened for editing", "51df826f-59d4-4287-a3ab-4dc90358eeea")

            o.inputAppendShitToExceedLength("title-0-5", const.file.maxTitleLen)
            o.inputPrependValue("details-0-5", "Fuck. ")
            o.buttonClick("primary-0-5")
            o.assertScreenHTML_todo("Error banner", "a1a1181d-3967-4770-a784-ade613a20792")

            o.inputSetValue("title-0-5", "A rather lame piece of... Trial 2")
            o.buttonClick("primary-0-5")
            o.assertScreenHTML_todo("Title and details changed", "7e053deb-06da-40f0-918a-c2166a858f83")

            o.section("Check that file remains unchanged") {
                o.assertScreenHTML_todo("10", "ed306a5c-ba62-43b2-afe9-c5747393ecdd")
            }
        }
    }

}


