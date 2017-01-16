package aps.front

import aps.*
import aps.front.testutils.*
import into.kommon.*
import kotlin.properties.Delegates.notNull

class TestSuite_UACustomer_Order_Files : TestSuite {
    override val shortDescription = null

    override val scenarios by lazy {listOf<TestScenario>(
        Test_UACustomer_Order_Files_1(),
        Test_UACustomer_DownloadForbiddenFile(),
        Test_UACustomer_LongFileList(),
        Test_UACustomer_Order_Files_Search(),
        Test_UACustomer_Order_Files_EditMeta(),
        Test_UACustomer_Order_Files_EditFile(),
        Test_UACustomer_Order_Files_EditFile_Error(),
        Test_UACustomer_Order_Files_AddFile(),
        Test_UACustomer_Order_Files_Misc()
    )}
}


class Test_UACustomer_Order_Files_1 : StepBasedTestScenario() {
    override fun buildSteps() {
        o.setUpOrderAndFiles1(shit)
        o.expectPieceOfShitDownload(PieceOfShitDownload(100001, "crazy monster boobs.rtf", forbidden = false, sha1 = "16428e392350fc2e0ea8dda708fdd4ed61f423d5")) {
            o.kicClick("download-100001")
        }
    }
}

class Test_UACustomer_DownloadForbiddenFile : StepBasedTestScenario() {
    override fun buildSteps() {
        o.setUpOrderAndFiles1(shit)
        o.acta {fuckingRemoteCall.executeSQL("Delete permission for crazy boobs file", """
            delete from file_user_permissions
            where
                file_id = (select id from files where name = 'crazy monster boobs.rtf') and
                user_id = (select id from users where first_name = 'Иво')
        """)}
        o.willWaitForModalShown()
        o.expectPieceOfShitDownload(PieceOfShitDownload(100001, "crazy monster boobs.rtf", forbidden = true, sha1 = "16428e392350fc2e0ea8dda708fdd4ed61f423d5")) {
            o.kicClick("download-100001")
        }
        o.waitForModalShown()
        o.assertScreenHTML("Forbidden modal", "eb1ec474-315e-4184-829e-91882afcdf87")
        o.clickModalOKAndWaitTillHidden()
        o.assertScreenHTML("Modal closed", "fb05d2d8-18f6-48a1-9dfc-bd18e212c498")
    }
}

class Test_UACustomer_LongFileList : StepBasedTestScenario() {
    val testShit = TestShit()

    override fun buildSteps() {
        o.setUpOrderFilesTestTemplate_1(
            testShit,
            setUpUsers = {
                o.setUpBobul(testShit)
            },
            setUpOrders = {o.setUpBobulOrder(
                testShit,
                setUpFiles = {oid-> async {
                    await(setUpFilesByBobul_1(testShit, oid))

                    val mnt = moment("2016-12-02 13:17:08")
                    for (i in 1..20) {
                        await(ImposeNextRequestTimestampRequest.send(mnt.formatPostgres()))
                        await(send(testShit.bobulToken, CustomerAddUAOrderFileRequest()-{o->
                            o.orderID.value = oid
                            o.file.content = FileField.Content.TestFileOnServer("shitty document $i.rtf")
                            o.title.value = "Some shitty document $i"
                            o.details.value = dedent("""
                                        This is shitty text $i.
                                        Фрау Грубах, у которой вид был вовсе не виноватый, отперла двери в  прихожей  перед  всей  компанией,  и  К.  по  привычке
                                        взглянул  на завязки фартука, которые слишком глубоко врезались в ее мощный стан. На улице К. поглядел на часы  и  решил  взять
                                        такси,  чтобы  не  затягивать еще больше получасовое опоздание.
                                    """)
                        }))
//                        await(fuckingRemoteCall.executeSQL("Add file permissions", """
//                                    insert into file_user_permissions(file_id, user_id) values(${100002 + i}, 100000)
//                                """))
                        mnt.add(23, "minutes").add(14, "seconds")
                    }
                }})
            },
            assertScreen = {o.assertScreenHTML("1", "acbe3d9e-32cc-426a-bb6a-f03b6c9a4a55")}
        )

        o.acta {TestUserActionAnimation.scroll(1570)}
        o.requestSequence(
            buildAction = {o.buttonClick("loadMore")},
            assertionDescr = "More stuff",
            halfwayAssertionID = "84a08d13-314d-4a8e-87e7-e04d34c54462",
            halfwayOpts = AssertScreenOpts(bannerVerticalPosition = VerticalPosition.TOP),
            finalAssertionID = "57da20ff-c245-4780-92fb-a432dd5956f8"
        )

        o.acta {TestUserActionAnimation.scroll(3620)}
        o.requestSequence(
            buildAction = {o.buttonClick("loadMore")},
            assertionDescr = "Even more stuff",
            halfwayAssertionID = "ae824d97-58ac-4ec1-8f05-ebdfe1907928",
            halfwayOpts = AssertScreenOpts(bannerVerticalPosition = VerticalPosition.TOP),
            finalAssertionID = "cdd171b3-bc05-481f-8bf0-15f6253b88a6"
        )

    }
}

class Test_UACustomer_Order_Files_Search : StepBasedTestScenario() {
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
                    setUpFiles = {oid-> async {
                        await(setUpFilesByBobul_1(shit, oid))
                        await(setUpFilesByFedor_1(shit, oid))
                        await(setUpFilesByBobul_2(shit, oid))
                    }})
            },
            assertScreen = {o.assertScreenHTML("1", "0f77828b-fe22-4606-8f97-0efecc6fefde")}
        )

        o.inputSetValue("search", "мышкин")
        o.inputPressEnter("search")
        o.assertScreenHTML("2", "561577ab-ad4b-4bb5-bf1d-f50e9eac2c68")

        o.inputSetValue("search", "мышкин настасья")
        o.inputPressEnter("search")
        o.assertScreenHTML("3", "df1e1441-9b97-40b9-8e87-ddcad49f5c06")

        o.inputSetValue("search", "искренний")
        o.inputPressEnter("search")
        o.assertScreenHTML("4", "bf5e705e-c181-477c-9c8e-d16866756f65")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.FROM_WRITER)
        o.assertScreenHTML("5", "c91c05d1-aedb-44f3-a00a-7875de20642e")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.FROM_ME)
        o.assertScreenHTML("6", "e6164186-36f8-465b-b7ff-81bc7739946a")

        o.selectSetValue("filter", CustomerFileFilter.values(), CustomerFileFilter.ALL)
        o.assertScreenHTML("7", "0ee8b2c5-9252-477a-a8f0-8e0ab6bd7e0d")

        o.inputSetValue("search", "жопа")
        o.inputPressEnter("search")
        o.assertScreenHTML("8", "e9e102ed-a6fd-4bd4-a4a9-98a9391e5920")

        o.inputSetValue("search", "одно сообщить")
        o.inputPressEnter("search")
        o.assertScreenHTML("9", "4c756d27-b32e-456c-85bd-72c18ed5c419")

        o.inputSetValue("search", "trial")
        o.inputPressEnter("search")
        o.assertScreenHTML("10", "fde730d2-10f9-4ba0-8abf-d2a5b275a7a8")

        o.inputSetValue("search", "boobs")
        o.inputPressEnter("search")
        o.assertScreenHTML("11", "7871923d-d3b4-4ed1-9c61-5dc2d29a269d")

        o.inputSetValue("search", "100004")
        o.inputPressEnter("search")
        o.assertScreenHTML("12", "5db37d2c-367f-4913-b9c1-7075ebe87bbc")

        o.inputSetValue("search", "100004 100000")
        o.inputPressEnter("search")
        o.assertScreenHTML("13", "b47d716a-9b85-4cc9-9ade-274c6e13fc32")
    }
}

abstract class TestUACustomer_Order_Files_Base : StepBasedTestScenario() {
    abstract fun buildSteps0()

    override fun buildSteps() {
        o.initialShit(this)
        o.setUpOrderFilesTestTemplate_1(
            shit,
            setUpUsers = {
                o.setUpBobul(shit)
                o.setUpFedor(shit)
                o.setUpDasja(shit)
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


class Test_UACustomer_Order_Files_EditMeta : TestUACustomer_Order_Files_Base() {
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

class Test_UACustomer_Order_Files_EditFile : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        val orderFileID = 100010L
        val fileID = 100008L

        fun expectChangedFileDownload() {
            o.expectPieceOfShitDownload(PieceOfShitDownload(fileID, "fuck you.rtf", forbidden = false, sha1 = "1f378c5775314852bb2f45fd52611b28812978e3")) {
                o.kicClick("download-$orderFileID")
            }
        }

        o.acta {TestUserActionAnimation.scroll(700)}
        o.kicClick("edit-$orderFileID")
        o.assertScreenHTML("Piece of shit #$orderFileID is opened for editing", "683e036b-0ecb-4d4d-be5a-1b2591a83abc")
        o.fileFieldChoose(
            assertionDescr = "File changed",
            assertionID = "b3afb796-7115-4f11-a980-acd48e898052",
            keySuffix = "-$orderFileID",
            fileName = "fuck you.rtf")
        o.inputSetValue("title-$orderFileID", "The Fuck You")
        o.inputPrependValue("details-$orderFileID", "A fucky piece of text. ")

        o.formWithAnimationOnCompletionSequence(
            shit,
            buildAction = {
                o.buttonClick("primary-$orderFileID")
            },
            assertionDescr = "Shit is saved",
            halfwayAssertionID = "485b79d2-0cb5-48ce-937e-3843b1b71f89",
            completionAnimationHalfwayAssertionID = "3402503d-c9ca-4fff-b9ab-1ef44896bd4f",
            finalAssertionID = "a34b038f-3578-411e-9aad-e8011755e0cb"
        )

        expectChangedFileDownload()

        o.kicClick("edit-$orderFileID")
        o.assertScreenHTML("Piece of shit #$orderFileID is opened at new position", "166e3d38-e981-42fc-92ab-47e5fce8eca1")
        o.inputAppendValue("title-$orderFileID", " (yes, you)")
        o.formWithAnimationOnCompletionSequence(
            shit,
            buildAction = {
                o.buttonClick("primary-$orderFileID")
            },
            assertionDescr = "Shit is saved again",
            halfwayAssertionID = "633010c2-2015-461c-850b-2aea3a74e7de",
            completionAnimationHalfwayAssertionID = "05837cd7-998e-4daa-aa11-4a04892e4458",
            finalAssertionID = "ac35950e-bfcf-4d18-8275-4fba17afd8fd"
        )

        expectChangedFileDownload()

        o.twoStepLockSequence(
            buildAction = {
                o.buttonClick(fconst.key.refreshPage.testRef)
            },
            lock = TestGlobal.loadPageForURLLock,
            assertionDescr = "Page refresh",
            assertionID1 = "9e3aaafd-8fc0-4580-9eb7-5319496c6278",
            assertionID2 = "36acd3be-3e16-4bc3-9968-4269fdb03fce"
        )
    }

}

class Test_UACustomer_Order_Files_AddFile : TestUACustomer_Order_Files_Base() {
//    override val useSnapshot = true

    override fun buildSteps0() {
        o.addFile(shit, "b31dc136-68f4-417c-bc2c-9e4088b28ac4", fileName = "tiny pussy.rtf", title = "The Tiny Little Pussy", details = "Details? What kind of fucking details?")
        o.addFile(shit, "fcbe57dc-5984-421c-9d27-32b50f0d3cbc", fileName = "little pussy.rtf", title = "Our Little Pussy", details = "The pussy grows")
        o.snapshot(Snapshot("1", "29ae913d-a54b-4ff5-a072-f8439e275cce"))
        o.addFile(shit, "808d747e-bd63-44d4-880e-84f2e2a10736", fileName = "monster pussy.rtf", title = "The Monster Pussy", details = "This is really serious pussy here")
        o.beginWorkRegion()

//        o.search(
//            assertionDescr = "Search newly added stuff"
//        )
    }

}

private fun TestScenarioBuilder.addFile(shit: TestShit, aid: String, fileName: String, title: String, details: String) {
    sequence(
        buildAction = {
            buttonClick(fconst.key.plus.testRef)
        },
        assertionDescr = "Opened plus form",
        steps = listOf(
            TestSequenceStep(TestGlobal.fadeHalfwayLock, "$aid--1"),
            TestSequenceStep(TestGlobal.fadeDoneLock, "$aid--2")
        )
    )

    fileFieldChoose(
        assertionDescr = "Chose file",
        assertionID = "$aid--3",
        keySuffix = "",
        fileName = fileName)

    inputAppendValue("title", title)
    inputAppendValue("details", details)
    submitForm(
        shit,
        assertionDescr = "Shit is added",
        buildAction = {
            buttonClick("primary")
        },
        tickingAssertionID = "$aid--4",
        doneAssertionID = "$aid--5"
    )
}

class Test_UACustomer_Order_Files_EditFile_Error : TestUACustomer_Order_Files_Base() {
    override fun buildSteps0() {
        imf()
    }
}

class Test_UACustomer_Order_Files_Misc : TestUACustomer_Order_Files_Base() {
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
                    o.acta {TestGlobal.animationHalfwaySignal.promise.orTestTimeout(1000)}
                    o.assertScreenHTML("Item vanishes (halfway)", "b06e4d3c-9bfa-4397-8d4d-76044c7bd9d6")
                    o.act {TestGlobal.animationHalfwaySignalProcessedSignal.resolve()}

                    o.acta {TestGlobal.shitVanished.promise.orTestTimeout(1000)}
                    o.assertScreenHTML("Item vanished", "04d13114-4773-43aa-8370-7213ac3651b1")
                }
            )
        }

        // TODO:vgrechka Reload page and check modifications
    }

}

//        o.beginWorkRegion()
//        o.endWorkRegion()


fun TestScenarioBuilder.setUpOrderAndFiles1(shit: TestShit) {
    val o = this
    o.setUpOrderFilesTestTemplate_1(
        shit,
        setUpUsers = {
            o.setUpBobul(shit)
        },
        setUpOrders = {
            o.setUpBobulOrder(shit, {oid->
                setUpFilesByBobul_1(shit, oid)
            })
        },
        assertScreen = {o.assertScreenHTML("setUpOrderAndFiles1", "f2c21456-9c7c-4fbf-94ca-ccc4544837a9")})
}

fun TestScenarioBuilder.setUpOrderFilesTestTemplate_1(shit: TestShit, setUpUsers: () -> Unit, setUpOrders: () -> Unit, assertScreen: () -> Unit) {
    val o = this

    o.acta {fedis.pushLogGroup("Prepare test shit")}
    setUpUsers()
    setUpOrders()
    o.acta {fedis.popLogGroup()}

    o.initFuckingBrowser(fillTypedStorageLocal = {
        it.token = shit.bobulToken
    })
    o.kindaNavigateToStaticContent("${testconst.url.customer}/order.html?id=100000&tab=files")
    o.assertCustomerBreatheScreen()

    o.acta {
        async {
            val world = World("boobs")
            await(world.boot())
        }
    }

    assertScreen()
}




