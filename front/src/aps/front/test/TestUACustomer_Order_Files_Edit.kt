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
        o.kicClick("edit-100008")
        o.assertScreenHTML_todo("Piece of shit 0-3 is opened for editing", "31c57c9d-06e2-43ee-beef-106f1689b954")

        o.section_rem("Edit some shit without changing file") {
            o.act {jqbody.scrollTop(900)}
            o.kicClick("edit-100006")
            o.assertScreenHTML_todo("Piece of shit is opened for editing", "51df826f-59d4-4287-a3ab-4dc90358eeea")

            o.inputAppendShitToExceedLength("title-100006", const.file.maxTitleLen)
            o.inputPrependValue("details-100006", "Fuck. ")
            o.buttonClick("primary-100006")
            o.assertScreenHTML_todo("Error banner", "a1a1181d-3967-4770-a784-ade613a20792")

            o.inputSetValue("title-100006", "A rather lame piece of... Trial 2")
            o.buttonClick("primary-100006")
            o.assertScreenHTML_todo("Title and details changed", "7e053deb-06da-40f0-918a-c2166a858f83")

            o.section("Check that file remains unchanged") {
                o.expectPieceOfShitDownload(PieceOfShitDownload(100006, "piece of trial 2.rtf", forbidden = false, sha1 = testconst.sha1.pieceOfTrial2)) {
                    o.kicClick("download-100006")
                }
            }
        }

        o.act {shit.pauses = TestShit.Pauses.ONLY}
        o.section("Cancelling editing") {
            o.act {jqbody.scrollTop(1200)}
            o.kicClick("edit-100004")
            o.assertScreenHTML_todo("Piece of shit is opened for editing", "34672ca0-c212-40a7-84e5-3cc53715041a")

            o.inputSetValue("title-100004", "Fuck you")
            o.inputSetValue("details-100004", "bitch")
            o.pause(shit, "Will click Cancel...")
            o.buttonClick("cancel-100004")
            o.assertScreenHTML_todo("Piece of shit is closed, nothing changed", "55b183c9-d6c7-489f-90be-4bc3c0c7550d")
        }

        o.buttonClick("loadMore")
        o.assertScreenHTML_todo("More items", "ab5858fe-82b0-4bbc-abce-eb8c2645c2aa")

        o.section("Delete some shit") {
            o.act {jqbody.scrollTop(2500)}

            o.modalSequence(
                action = {
                    o.kicClickNoWait("delete-100001")
                },
                assertModal = {
                    o.assertScreenHTML_todo("Warning modal", "7f6f306f-23a5-430e-ab6e-4b3231989fae")
                    o.pause(shit, only = false)
                },
                modalAction = {
                    o.buttonClick("modal-no")
                },
                assertAfterModal = {
                    o.assertScreenHTML_todo("Item is alive", "ef5083bd-971d-4ef9-a6e5-223ed706a05c")
                }
            )

            o.modalSequence(
                action = {
                    o.kicClickNoWait("delete-100001")
                },
                assertModal = {
                    o.assertScreenHTML_todo("Warning modal", "8b94712e-8a32-4769-aa88-f38c61ae83c0")
                    o.pause(shit, only = false)
                },
                modalAction = {
                    o.buttonClick("modal-yes")
                },
                assertAfterModal = {
                    o.assertScreenHTML_todo("Item vanished", "b06e4d3c-9bfa-4397-8d4d-76044c7bd9d6")
                }
            )
        }

        // TODO:vgrechka Reload page and check modifications
    }

}



