package aps.front

import aps.*
import aps.front.testutils.*

abstract class TestUACustomer_Order_Files_Base : StepBasedTestScenario() {
    abstract fun buildSteps0()

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

        buildSteps0()
    }
}

class TestUACustomer_Order_Files_EditMeta : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        o.kicClick("edit-100010")
        o.assertScreenHTML("Piece of shit #100010 is opened for editing", "234d53c3-19ae-4d2d-9172-61b37c5c4b29")
        o.inputSetValue("title-100010", "The Cunt")
        o.inputPrependValue("details-100010", "A story about cunt follows. ")

        o.formSequence(
            buildAction = {
                o.buttonClick("primary-100010")
            },
            assertionDescr = "Shit is saved",
            halfwayAssertionID = "7a4fb403-1698-45a8-931c-b66491e03c79",
            finalAssertionID = "86ca87fc-eed0-4da1-813c-8b9a8998c8ce"
        )
    }
}

class TestUACustomer_Order_Files_EditFile : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        o.kicClick("edit-100010")
        o.assertScreenHTML("Piece of shit #100010 is opened for editing", "683e036b-0ecb-4d4d-be5a-1b2591a83abc")
        o.buttonUserInitiatedClick("upload-100010")
        o.typeIntoOpenFileDialog("${testconst.filesRoot}fuck you.rtf")
        o.fileFieldWaitTillShitChanged("file-100010")
        o.assertScreenHTML("File changed", "b3afb796-7115-4f11-a980-acd48e898052")
        o.inputSetValue("title-100010", "The Fuck You")
        o.inputPrependValue("details-100010", "A fucky piece of text. ")

        o.formSequence(
            buildAction = {
                 o.buttonClick("primary-100010")
            },
            assertionDescr = "Shit is saved",
            halfwayAssertionID = "485b79d2-0cb5-48ce-937e-3843b1b71f89",
            finalAssertionID = "a34b038f-3578-411e-9aad-e8011755e0cb"
        )
    }
}

class TestUACustomer_Order_Files_Misc : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        o.acta {TestUserActionAnimation.scroll(700)}
        o.kicClick("edit-100008")
        o.assertScreenHTML("Piece of shit #100008 is opened for editing", "31c57c9d-06e2-43ee-beef-106f1689b954")

        o.section_rem("Edit some shit without changing file") {
            o.acta {TestUserActionAnimation.scroll(900)}
            o.kicClick("edit-100006")
            o.assertScreenHTML("Piece of shit is opened for editing", "51df826f-59d4-4287-a3ab-4dc90358eeea")

            o.inputAppendShitToExceedLength("title-100006", const.file.maxTitleLen)
            o.inputPrependValue("details-100006", "Fuck. ")
            o.buttonClick("primary-100006")
            o.assertScreenHTML("Error banner", "a1a1181d-3967-4770-a784-ade613a20792")

            o.inputSetValue("title-100006", "A rather lame piece of... Trial 2")
            o.buttonClick("primary-100006")
            o.assertScreenHTML("Title and details changed", "7e053deb-06da-40f0-918a-c2166a858f83")

            o.section("Check that file remains unchanged") {
                o.expectPieceOfShitDownload(PieceOfShitDownload(100006, "piece of trial 2.rtf", forbidden = false, sha1 = testconst.sha1.pieceOfTrial2)) {
                    o.kicClick("download-100006")
                }
            }
        }

        o.section("Cancelling editing") {
            o.acta {TestUserActionAnimation.scroll(1413)}
            o.kicClick("edit-100004", HandOpts(direction = HandDirection.DOWN))
            o.assertScreenHTML("Piece of shit #100004 is opened for editing", "34672ca0-c212-40a7-84e5-3cc53715041a")

            o.inputSetValue("title-100004", "Fuck you")
            o.inputSetValue("details-100004", "bitch")
            o.animatedActionSequence(
                buildAction = {
                    o.buttonClick("cancel-100004", HandOpts(pauseDescr = "Will discard edits..."))
                },
                assertionDescr = "Piece of shit is closed, nothing changed",
                halfwayAssertionID = "16e1707d-3c9f-4f6a-934b-ced0620d364e",
                finalAssertionID = "55b183c9-d6c7-489f-90be-4bc3c0c7550d"
            )
        }

        o.acta {TestUserActionAnimation.scroll(2290)}
        o.requestSequence(
            buildAction = {o.buttonClick("loadMore", HandOpts(direction = HandDirection.DOWN))},
            assertionDescr = "Got more items",
            halfwayAssertionID = "7783b7fa-55eb-4972-a506-09e9f9d17371",
            finalAssertionID = "665b1fb9-7021-4bd0-8ba8-59e4a3cc5af6",
            halfwayOpts = AssertScreenOpts(bannerVerticalPosition = VerticalPosition.TOP)
        )

        o.section("Delete some shit") {
            //            o.acta {TestUserActionAnimation.scroll(2200)}

            o.modalSequence(
                action = {
                    o.kicClick("delete-100001")
                },
                assertModal = {
                    o.assertScreenHTML("Warning modal", "7f6f306f-23a5-430e-ab6e-4b3231989fae")
                },
                modalAction = {
                    o.buttonClick("modal-no")
                },
                assertAfterModal = {
                    o.assertScreenHTML("Item is alive", "ef5083bd-971d-4ef9-a6e5-223ed706a05c")
                }
            )

            o.modalSequence(
                action = {
                    o.act {
                        TestGlobal.animationHalfwaySignal = ResolvableShit()
                        TestGlobal.animationHalfwaySignalProcessedSignal = ResolvableShit()
                        TestGlobal.shitVanished = ResolvableShit()
                    }

                    o.kicClick("delete-100001")
                },
                assertModal = {
                    o.assertScreenHTML("Warning modal", "8b94712e-8a32-4769-aa88-f38c61ae83c0")
                },
                modalAction = {
                    o.acta {send(ImposeNextRequestErrorRequest())}
                    o.requestSequence(
                        buildAction = {o.buttonClick("modal-yes")},
                        assertionDescr = "Backend error",
                        halfwayAssertionID = "4b6e09ea-f0a7-4bfb-a4d7-e1785889cc9c",
                        finalAssertionID = "5a0cdafa-37b0-45dc-86fb-b4645636a129"
                    )

                    o.requestSequenceNoFinalAssertion(
                        buildAction = {o.buttonClick("modal-yes")},
                        assertionDescr = "Backend OK",
                        halfwayAssertionID = "ddfede30-0595-43cb-a460-9c07d75a7920"
                    )
                },
                assertAfterModal = {
                    o.acta {TestGlobal.animationHalfwaySignal.promise.orTimeout(1000)}
                    o.assertScreenHTML("Item vanishes (halfway)", "b06e4d3c-9bfa-4397-8d4d-76044c7bd9d6")
                    o.act {TestGlobal.animationHalfwaySignalProcessedSignal.resolve()}

                    o.acta {TestGlobal.shitVanished.promise.orTimeout(1000)}
                    o.assertScreenHTML("Item vanished", "04d13114-4773-43aa-8370-7213ac3651b1")
                }
            )
        }

        // TODO:vgrechka Reload page and check modifications
    }

}

//        o.beginWorkRegion()
//        o.endWorkRegion()


