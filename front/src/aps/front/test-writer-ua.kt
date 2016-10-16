/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*

class TS_UA_Writer_SignUp_1 : TestScenario() {
    override fun run(): Promise<Unit> {"__async"
        val slowly = false

        if (slowly) {
            jshit.setTestSpeed("slow")
            jshit.art.respectArtPauses = true
        }

//        __await<dynamic>(jshit.art.resetTestDatabase(json("templateDB" to "test-template-ua-1", "alsoRecreateTemplate" to true)))
        __await(ResetTestDatabaseRequest.send(templateDB = "test-template-ua-1", recreateTemplate = true))

        val instructions = mutableListOf<TestInstruction>()
        instructions.addAll(vovchok1())
        instructions.add(TestInstruction.WorldPoint("1"))
        instructions.addAll(dasja1())
        instructions.add(TestInstruction.WorldPoint("2"))
        instructions.addAll(vovchok2())
        instructions.addAll(dasja2())
        instructions.add(TestInstruction.WorldPoint("3"))
        instructions.addAll(vovchok3())
        instructions.addAll(dasja3())
        instructions.addAll(vovchok4())
        instructions.add(TestInstruction.WorldPoint("4"))
        instructions.addAll(dasja4())

        return __await<dynamic>(art.run(instructions))
    }

    fun vovchok1(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Marko Vovchok signs up") {

                + selectBrowser("WRITER", "vovchok1", "Marko Vovchok, an eager writer wannabe, comes to our site")

                action("Trying to sign in (to non-existing account)") {
                    + signIn("vovchok@test.shit.ua", "something")
                }
                state("Of course it failed") {
                    assertGen("1a543472-b429-4add-88e8-799598607ad3")
                }

                action("Clicking 'Sign Up' link") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("TextField-password.Input", "something")
                    click("link-createAccount", "2016-08-12 20:40:58")
                }
                state("Got registration form") {
                    assertGen("877a3f2f-ad7d-41c7-af4b-9665526fc27f")
                }

                section("Submitting sign-up form") {
                    action("Trying to submit empty form") {
                        click("button-primary", "2016-08-14 12:55:19")
                    }
                    state("Got a bunch of errors") {
                        assertGen("9b0cad6b-c396-4b8a-b731-57905900a80a")
                    }

                    action("Agreeing terms") {
                        setValue("AgreeTermsField.Checkbox", true)
                        click("button-primary", "2016-08-14 14:22:04")
                    }
                    state("-1 error message") {
                        assertGen("7dbefeef-84f6-47e2-8635-d67ddcbb153d")
                    }

                    action("Entering email") {
                        setValue("TextField-email.Input", "vovchok@test.shit.ua")
                        click("button-primary", "2016-08-14 14:23:51")
                    }
                    state("-1 error message") {
                        assertGen("ac403942-31f7-45a3-b42c-e37f6c1b83a2")
                    }

                    action("Entering first name") {
                        setValue("TextField-firstName.Input", "Марко")
                        click("button-primary", "2016-08-14 14:25:32")
                    }
                    state("-1 error message") {
                        assertGen("f866c286-58f4-4785-a2da-bad213bd4567")
                    }

                    action("Entering last name") {
                        setValue("TextField-lastName.Input", "Вовчок")
                        - TestInstruction.Do({"__async"
                            __await(ImposeNextGeneratedPasswordRequest.send("fucking-big-generated-secret")) /ignora
//                            __await<dynamic>(drpc(json("fun" to "danger_imposeNextGeneratedPassword", "password" to "fucking-big-generated-secret")))
                        })

                        click("button-primary", "2016-08-14 17:48:51")
                    }
                    state("Sign-in form with message saying that account is created. Got email with password") {
                        assertGen("0b7e96bf-4f42-41dc-b658-7b60e863356e")
                    }

                    - TestInstruction.Do({"__async"
                        __await(ClearSentEmailsRequest.send()) /ignora
//                        __await<dynamic>(drpc(json("fun" to "danger_clearSentEmails")))
                    })
                }

                section("Submitting sign-in form") {
                    action("First try with wrong password") {
                        setValue("TextField-email.Input", "vovchok@test.shit.ua")
                        setValue("TextField-password.Input", "dead-wrong-shit")
                        click("button-primary", "2016-08-15 19:26:34")
                    }
                    state("No way") {
                        assertGen("51815ec8-62e9-4c25-aad5-fa97f30d6cff")
                    }

                    action("Now with password from received email") {
                        setValue("TextField-email.Input", "vovchok@test.shit.ua")
                        setValue("TextField-password.Input", "fucking-big-generated-secret")
                        click("button-primary", "2016-08-15 19:48:57")
                    }
                    state("Got page asking to fill profile") {
                        assertGen("00997d56-f00a-4b6a-bffd-595ba4c23806")
                    }
                }

                section("Submitting profile") {
                    action("Trying to send empty profile") {
                        setValue("TextField-phone.Input", "")
                        setValue("TextField-aboutMe.Input", "")
                        click("button-primary", "2016-08-15 20:12:37")
                    }
                    state("Bunch of errors") {
                        assertGen("4fc3f3d3-2806-4c99-9769-6d046cd7a985")
                    }

                    action("Entering phone: some junk") {
                        setValue("TextField-phone.Input", "qwerty")
                        click("button-primary", "2016-08-15 20:16:20")
                    }
                    state("No way") {
                        assertGen("243f5834-48a4-496b-8770-371ac0c94b19")
                    }

                    action("Entering phone: too long") {
                        setValue("TextField-phone.Input", "012345678901234567890")
                        click("button-primary", "2016-08-16 11:56:01")
                    }
                    state("No way") {
                        assertGen("a07d90e4-60a6-45c1-a5c6-cd217a01a933")
                    }

                    action("Entering phone: too few digits") {
                        setValue("TextField-phone.Input", "--3234--++--")
                        click("button-primary", "2016-08-16 11:56:05")
                    }
                    state("No way") {
                        assertGen("3f290096-9162-419b-835d-ec2b02f8f5ca")
                    }

                    action("Entering phone: something sane") {
                        setValue("TextField-phone.Input", "+38 (068) 9110032")
                        click("button-primary", "2016-08-16 12:02:30")
                    }
                    state("-1 error") {
                        assertGen("d072d3c4-38c9-481a-9fec-0ecd9c5601a2")
                    }

                    action("Entering about: too long") {
                        setValue("TextField-aboutMe.Input", dynamicAsString(testCommon.LONG_SHIT_301))
                        click("button-primary", "2016-08-16 14:01:33")
                    }
                    state("TODO State description") {
                        assertGen("e3c1c929-27af-4742-850c-1c1b7cb2ff51")
                    }

                    action("Entering aboutMe: something sane") {
                        setValue("TextField-aboutMe.Input", "I am a fucking bitch. No, really. Wanna have one for the team?")
                        click("button-primary", "2016-08-16 14:55:45")
                    }
                    state("TODO State description") {
                        assertGen("9219ded3-54e4-4d6b-bf1b-63615cf8a56e")
                    }
                }
            }
        }
    }

    fun dasja1(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Dasja accepts that shitty profile") {
                + selectBrowser("WRITER", "dasja1", "Dasja, a support admin, comes into play")

                action("Normal sign in") {
                    + signIn("dasja@test.shit.ua", "secret")
                }
                state("TODO") {
                    assertGen("659a49bb-8018-4ac1-8c64-0fa31e998a50")
                }

                action("Click on 'Approve Profiles' link") {
                    click("section-workPending.profilesToApprove.link", "2016-08-18 14:58:16")
                }
                state("Got page with profiles to be approved") {
                    assertGen("dd32f775-d45f-4a86-b815-c8897fa01fe3",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name))
                }

                action("Click on edit icon") {
                    click("chunk-i000.item-i000.heading.icon-edit", "2016-08-26 13:56:04")
                }
                state("Got form") {
                    assertGen("ba17b066-b272-48eb-901c-d22b5b1f803b", expectAll(
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name),
                        expectItemEditorFormControls(state="PROFILE_APPROVAL_PENDING")))
                }

                action("Make some changes and approve") {
                    setValue("chunk-i000.item-i000.SelectField-state.Select", UserFilter.COOL.name)
                    setValue("chunk-i000.item-i000.TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("chunk-i000.item-i000.TextField-firstName.Input", "Маркожопик")
                    setValue("chunk-i000.item-i000.TextField-lastName.Input", "Вовкулака")
                    setValue("chunk-i000.item-i000.TextField-phone.Input", "+38 (068) 9110032")
                    setValue("chunk-i000.item-i000.TextField-aboutMe.Input", "I am a fucking bitch. No, really. Wanna have one for the team?")
                    setValue("chunk-i000.item-i000.TextField-adminNotes.Input", "And she really is...")
                    click("chunk-i000.item-i000.button-primary", "2016-08-26 14:14:26")
                }
                state("That bitch is now cool") {
                    assertGen("59308ccf-67b9-4fab-a13d-154c96e8bd63",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name))
                }

                action("Press enter in search box to refresh shit") {
                    keyDown("Input-search", 13)
                }
                state("Now we get nothing, cause there’s no shit to approve") {
                    assertGen("ffd89ee7-31b5-4742-b15a-196876ebec6f",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name))
                }

                action("Choose 'All' to see all shit") {
                    setValue("Select-filter", UserFilter.ALL.name)
                }
                state("Got all shit. That bitch is cool") {
                    assertGen("88b14624-383f-4edf-8b3e-a9c413d87f27",
                        expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name))
                }
            }
        }
    }

    fun vovchok2(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Marko Vovchok can use site") {
                + selectBrowser("WRITER", "vovchok2", "Marko Vovchok, aka The Bitch, comes again some time later")

                action("Sign in") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("TextField-password.Input", "fucking-big-generated-secret")
                    click("button-primary", "2016-08-27 12:11:34")
                }
                state("Got something") {
                    assertGen("86e56915-a334-4209-85c9-8d2c53cd9f0a")
                }
            }
        }
    }

    fun dasja2(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Dasja rejects the bitch") {
                + selectBrowser("WRITER", "dasja2", "Dasja decides to reject the bitch for whatever reasons")

                action("Normal sign in") {
                    + signIn("dasja@test.shit.ua", "secret")
                }
                state("Got dashboard") {
                    assertGen("908a5f71-6432-4a8d-8efd-922b25ed54b5")
                }

                action("Select 'Users' in top navbar") {
                    click("TopNavItem-admin-users", "2016-08-27 13:17:16")
                }
                state("Got users") {
                    assertGen("6fbd591f-f62c-4a37-a6ae-0958ef6c81f7",
                        expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name))
                }

                action("Click on edit icon for the bitch") {
                    click("chunk-i000.item-i000.heading.icon-edit", "2016-08-27 13:20:58")
                }
                state("TODO State description") {
                    assertGen("b138801b-9fec-43b3-94d6-f135318b2a22",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state=UserFilter.COOL.name)))
                }

                // @wip rejection
                action("Choose 'Rejected' status") {
                    setValue("chunk-i000.item-i000.SelectField-state.Select", "PROFILE_REJECTED")
                }
                state("Rejection reason field appears") {
                    assertGen("fcdac58b-e864-4a21-b3a2-47f29a1fbba9",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state="PROFILE_REJECTED")))
                }

                action("Enter something into rejection reason, then change 'Rejected' to something else") {
                    setValue("chunk-i000.item-i000.TextField-profileRejectionReason.Input", "Well... er...")
                    setValue("chunk-i000.item-i000.SelectField-state.Select", UserFilter.COOL.name)
                }
                state("Rejection reason field disappears") {
                    assertGen("a81d2f49-aa12-45ea-92a2-fc6bf4ad0cf6",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state=UserFilter.COOL.name)))
                }

                action("Select 'Rejected' again") {
                    setValue("chunk-i000.item-i000.SelectField-state.Select", "PROFILE_REJECTED")
                }
                state("Rejection reason is kept") {
                    assertGen("2e650433-d5f6-4c94-ba5a-b3ff07e15865",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state="PROFILE_REJECTED")))
                }

                action("Try to reject without reason") {
                    setValue("chunk-i000.item-i000.TextField-profileRejectionReason.Input", "")
                    click("chunk-i000.item-i000.button-primary", "2016-08-27 13:23:05")
                }
                state("No way") {
                    assertGen("c2f5de3e-ff25-4c38-80dc-e09d89e0944f",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state="PROFILE_REJECTED")))
                }

                action("Reject motherfucker along with changing name and email") {
                    setValue("chunk-i000.item-i000.TextField-email.Input", "vovkulaka@test.shit.ua")
                    setValue("chunk-i000.item-i000.TextField-firstName.Input", "Мокрожопик")
                    setValue("chunk-i000.item-i000.TextField-profileRejectionReason.Input", "Why I do this? Cause I can! I'm the admin! U-ha-ha-ha...")
                    click("chunk-i000.item-i000.button-primary", "2016-08-27 13:23:07")
                }
                state("The motherfucker is now reddish") {
                    assertGen("0d4d388f-a0ad-4824-9a37-36e7e74365ae",
                        expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name))
                }

                action("Choose to see only cool users") {
                    setValue("Select-filter", UserFilter.COOL.name)
                }
                state("No bitch in the list") {
                    assertGen("c9219504-6a67-4863-b09c-c6eafa80bc26",
                        expectHeaderControls(search="", filter=UserFilter.COOL.name, ordering=Ordering.DESC.name))
                }

                action("Choose to see only rejected users") {
                    setValue("Select-filter", UserFilter.PROFILE_REJECTED.name)
                }
                state("Only bitch is in the list") {
                    assertGen("d21b167c-3064-41b7-a3d3-d8a7c5f91b92",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_REJECTED.name, ordering=Ordering.DESC.name))
                }

                action("Choose to see all") {
                    setValue("Select-filter", UserFilter.ALL.name)
                }
                state("Bitch and others are in the list") {
                    assertGen("d72124f6-0ad2-4f50-ba0e-7bedff1e6a79",
                        expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name))
                }
            }
        }
    }

    fun vovchok3(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Marko Vovchok faces she’s rejected") {
                + selectBrowser("WRITER", "vovchok3", "The Bitch opens browser")

                action("Try to sign in with old email") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("TextField-password.Input", "fucking-big-generated-secret")
                    click("button-primary", "2016-08-27 14:53:22")
                }
                state("No way, cause admin have changed her email to 'vovkulaka...'") {
                    assertGen("eeb4fc2a-0e24-4fc0-9e66-6ecccd932d8e")
                }

                action("Sign in with new email") {
                    setValue("TextField-email.Input", "vovkulaka@test.shit.ua")
                    setValue("TextField-password.Input", "fucking-big-generated-secret")
                    click("button-primary", "2016-08-27 14:54:22")
                }
                state("Oh, they rejected me") {
                    assertGen("f80aea79-7308-444b-acd3-fe81cee46eff")
                }

                action("Improve 'About Me' section") {
                    setValue("TextField-aboutMe.Input", "Пишу тексты за еду. Любые стили и направления.")
                    click("button-primary", "2016-08-28 01:19:00")
                }
                state("Got waiting screen") {
                    assertGen("35a2c077-01b2-407c-beba-4debb789d07d")
                }
            }
        }
    }

    fun dasja3(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Dasja accepts improved profile") {
                + selectBrowser("WRITER", "dasja3", "Dasja comes to do some work")

                action("Normal sign in") {
                    + signIn("dasja@test.shit.ua", "secret")
                }
                state("Got dashboard") {
                    assertGen("e72560ca-3f35-423a-8d8b-e65006eb26f0")
                }

                action("Click on 'Profiles to approve'") {
                    click("section-workPending.profilesToApprove.link", "2016-08-28 01:33:48")
                }
                state("Bitch somewhat improved her profile") {
                    assertGen("48b2f0d2-a1f0-446d-8c10-550b9abfd3e3",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name))
                }

                action("Click on pencil") {
                    click("chunk-i000.item-i000.heading.icon-edit", "2016-08-28 01:56:33")
                }
                state("Edit form opens") {
                    assertGen("18f8286c-ea61-4820-95c0-9ce3a129edfb",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state="PROFILE_APPROVAL_PENDING")))
                }

                action("Accept now") {
                    setValue("chunk-i000.item-i000.SelectField-state.Select", UserFilter.COOL.name)
                    setValue("chunk-i000.item-i000.TextField-adminNotes.Input", "Free workforce. Good. We'll exploit her to death. U-ha-ha-ha...")
                    click("chunk-i000.item-i000.button-primary", "2016-08-28 01:59:30")
                }
                state("She is cool now") {
                    assertGen("5d82460a-268f-43d8-bd00-e04dd671029c",
                        expectHeaderControls(search="", filter=UserFilter.PROFILE_APPROVAL_PENDING.name, ordering=Ordering.DESC.name))
                }
            }
        }
    }

    fun vovchok4(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Marko Vovchok can finally enter") {
                + selectBrowser("WRITER", "vovchok4", "The Bitch opens browser")

                action("Sign in") {
                    setValue("TextField-email.Input", "vovkulaka@test.shit.ua")
                    setValue("TextField-password.Input", "fucking-big-generated-secret")
                    click("button-primary", "2016-08-28 02:04:24")
                }
                state("Great, they accepted my profile") {
                    assertGen("5c4d0c0c-8e1f-4dfc-898d-dec7c3e6bbf7")
                }
            }
        }
    }

    fun dasja4(): Iterable<TestInstruction> {
        return buildPieceOfTest {
            section("Dasja bans bitch") {
                + selectBrowser("WRITER", "dasja4", "Dasja comes to ban that bitch")

                action("Normal sign in") {
                    + signIn("dasja@test.shit.ua", "secret")
                }
                state("Got dashboard") {
                    assertGen("6bd7cea2-b181-4978-b278-31a86086606f")
                }

                action("Click on 'Users'") {
                    click("TopNavItem-admin-users", "2016-08-28 08:36:33")
                }
                state("Got users, bitch is here") {
                    assertGen("4c7fa9d2-c993-4fdf-91e6-fc7e45d49e3b",
                        expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name))
                }

                action("Click on pencil to edit bitch") {
                    click("chunk-i000.item-i000.heading.icon-edit", "2016-08-28 08:38:01")
                }
                state("Got editing form") {
                    assertGen("4290992b-9d2b-4ab2-97e6-e32c87e9ea0c",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state=UserFilter.COOL.name)))
                }

                action("Choose 'Banned' from the state list") {
                    setValue("chunk-i000.item-i000.SelectField-state.Select", UserFilter.BANNED.name)
                }
                state("Ban reason field appears") {
                    assertGen("fa31639d-adae-4ba5-8b47-a58b75f6e17a",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state=UserFilter.BANNED.name)))
                }

                action("Try to submit without giving a reason") {
                    click("chunk-i000.item-i000.button-primary", "2016-08-28 10:00:48")
                }
                state("No way") {
                    assertGen("4ec74b0c-df11-4228-a327-92b4a3c1ac64",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name),
                            expectItemEditorFormControls(state=UserFilter.BANNED.name)))
                }

                action("Give some reason") {
                    setValue("chunk-i000.item-i000.TextField-banReason.Input", "Мы тебя больше не хотим")
                    click("chunk-i000.item-i000.button-primary", "2016-08-28 10:01:48")
                }
                state("She is very red now") {
                    assertGen("06325345-e660-4f6c-989c-aafdbac35d55",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.ALL.name, ordering=Ordering.DESC.name)))
                }

                action("Show only 'Cool'") {
                    setValue("Select-filter", UserFilter.COOL.name)
                }
                state("Got no bitch") {
                    assertGen("0213b214-02e0-41ce-9596-a4aa4d50389e",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.COOL.name, ordering=Ordering.DESC.name)))
                }

                action("Show only 'Banned'") {
                    setValue("Select-filter", UserFilter.BANNED.name)
                }
                state("Got bitch") {
                    assertGen("ae6f3952-0a58-4542-8c48-68c6654e6626",
                        expectAll(
                            expectHeaderControls(search="", filter=UserFilter.BANNED.name, ordering=Ordering.DESC.name)))
                }
            }
        }
    }

    fun selectBrowser(clientKind: String, browserName: String, stateDescription: String): Iterable<TestInstruction> {
        val res = mutableListOf(
            TestInstruction.Step("navigation", "Trying to open dashboard page as " + browserName),
            //            jsonTestItem("step" to json("kind" to "navigation", "long" to "Trying to open dashboard page as " + browserName)),

            TestInstruction.Do({"__async"
                global.CLIENT_KIND = clientKind
                host.selectBrowser(browserName)
                __await<dynamic>(host.navigate("dashboard.html"))
            }),

            TestInstruction.Step("state", stateDescription)
        )

        if (clientKind == "WRITER") {
            res.addAll(listOf(
                TestInstruction.AssertFuck("42816503-1dc0-4e43-aaa4-a8b11a680501", json(
                    "TextField-email.Input" to "",
                    "TextField-email.label" to "Почта",
                    "TextField-password.Input" to "",
                    "TextField-password.Input.type" to "password",
                    "TextField-password.label" to "Пароль",
                    "button-primary.title" to "Войти",
                    "link-createAccount.title" to "Срочно создать!",
                    "pageHeader.title" to "Вход",
                    "topNavLeft.TopNavItem-i000.shame" to "TopNavItem-why",
                    "topNavLeft.TopNavItem-i000.title" to "Почему мы?",
                    "topNavLeft.TopNavItem-i001.shame" to "TopNavItem-prices",
                    "topNavLeft.TopNavItem-i001.title" to "Цены",
                    "topNavLeft.TopNavItem-i002.shame" to "TopNavItem-faq",
                    "topNavLeft.TopNavItem-i002.title" to "ЧаВо",
                    "topNavRight.TopNavItem-i000.active" to true,
                    "topNavRight.TopNavItem-i000.shame" to "TopNavItem-sign-in",
                    "topNavRight.TopNavItem-i000.title" to "Вход",
                    "url" to "http://aps-ua-writer.local:3022/sign-in.html"
                ))
            ))
        } else if (clientKind == "CUSTOMER") {
            res.addAll(listOf(
                TestInstruction.AssertFuck("4d7bfd1d-d5cd-4b64-a069-a99f85f934da", json(
                    "Input-email" to "",
                    "Input-password" to "",
                    "TopNavItem-blog" to json("title" to "Блог"),
                    "TopNavItem-contact" to json("title" to "Связь"),
                    "TopNavItem-faq" to json("title" to "ЧаВо"),
                    "TopNavItem-prices" to json("title" to "Цены"),
                    "TopNavItem-samples" to json("title" to "Примеры"),
                    "TopNavItem-sign-in" to json("active" to true, "title" to "Вход"),
                    "TopNavItem-why" to json("title" to "Почему мы?"),
                    "button-primary" to json("title" to "Войти"),
                    "link-createAccount" to json("title" to "Срочно создать!"),
                    "pageHeader" to "Вход",
                    "url" to "http://aps-ua-writer.local:3022/sign-in.html"
                ))
            ))
        } else {
            throw js.Error("WTF is the clientKind")
        }

        return res
    }
}

fun dynamicAsString(x: dynamic): String {
    return x
}

@native interface StackAsError

fun captureStackAsError(): StackAsError {
    return js("Error()")
}

fun signIn(email: String, password: String): Iterable<TestInstruction> {
    return listOf(
        TestInstruction.SetValue("TextField-email.Input", email),
        TestInstruction.SetValue("TextField-password.Input", password),
        TestInstruction.Click("button-primary")
    )
}

fun expectItemEditorFormControls(state: String, chunkIndex: String = "000", itemIndex: String = "000"): ExpectationExtender {
    return {arg: dynamic ->
        val expected = arg.expected

        global.Object.assign(expected, json(
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i000.title" to "Прохладный",
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i000.value" to UserState.COOL.name,
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i001.title" to "Без профиля",
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i001.value" to UserState.PROFILE_PENDING.name,
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i002.title" to "Ждет аппрува профиля",
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i002.value" to UserState.PROFILE_APPROVAL_PENDING.name,
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i003.title" to "Профиль завернут",
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i003.value" to UserState.PROFILE_REJECTED.name,
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i004.title" to "Забанен",
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.item-i004.value" to UserState.BANNED.name,
            "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.label" to "Статус"
        ))

        if (state === UserFilter.COOL.name) {
            global.Object.assign(expected, json(
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title" to "Прохладный",
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value" to UserFilter.COOL.name
            ))
        }
        else if (state === "PROFILE_APPROVAL_PENDING") {
            global.Object.assign(expected, json(
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title" to "Ждет аппрува профиля",
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value" to "PROFILE_APPROVAL_PENDING"
            ))
        }
        else if (state === "PROFILE_REJECTED") {
            global.Object.assign(expected, json(
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title" to "Профиль завернут",
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value" to "PROFILE_REJECTED"
            ))
        }
        else if (state === UserFilter.BANNED.name) {
            global.Object.assign(expected, json(
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.title" to "Забанен",
                "chunk-i${chunkIndex}.item-i${itemIndex}.SelectField-state.Select.selected.value" to UserFilter.BANNED.name
            ))
        }
        else {
            Shitus.raise("Weird state: ${state}")
        }
    }
}

typealias ExpectationExtender = (dynamic) -> Unit

fun expectAll(vararg fs: ExpectationExtender): ExpectationExtender {
    return {arg: dynamic ->
        for (f in fs) {
            f(arg)
        }
    }
}

fun expectHeaderControls(search: String, filter: String, ordering: String): ExpectationExtender {
    return {arg: dynamic ->
        val expected = arg.expected

        global.Object.assign(expected, json(
            "Input-search" to search,

            "Select-filter.item-i000.title" to "Все",
            "Select-filter.item-i000.value" to UserFilter.ALL.name,
            "Select-filter.item-i001.title" to "Прохладные",
            "Select-filter.item-i001.value" to UserFilter.COOL.name,
            "Select-filter.item-i002.title" to "Ждут аппрува",
            "Select-filter.item-i002.value" to UserFilter.PROFILE_APPROVAL_PENDING.name,
            "Select-filter.item-i003.title" to "Завернутые",
            "Select-filter.item-i003.value" to UserFilter.PROFILE_REJECTED.name,
            "Select-filter.item-i004.title" to "Забаненые",
            "Select-filter.item-i004.value" to UserFilter.BANNED.name,

            "Select-ordering.item-i000.title" to "Сначала старые",
            "Select-ordering.item-i000.value" to Ordering.ASC.name,
            "Select-ordering.item-i001.title" to "Сначала новые",
            "Select-ordering.item-i001.value" to Ordering.DESC.name,
            "Select-ordering.selected.title" to "Сначала новые",
            "Select-ordering.selected.value" to Ordering.DESC.name
        ))

        if (filter == UserFilter.ALL.name) {
            global.Object.assign(expected, json(
                "Select-filter.selected.title" to "Все",
                "Select-filter.selected.value" to UserFilter.ALL.name
            ))
        }
        else if (filter == UserFilter.COOL.name) {
            global.Object.assign(expected, json(
                "Select-filter.selected.title" to "Прохладные",
                "Select-filter.selected.value" to UserFilter.COOL.name
            ))
        }
        else if (filter == UserFilter.PROFILE_APPROVAL_PENDING.name) {
            global.Object.assign(expected, json(
                "Select-filter.selected.title" to "Ждут аппрува",
                "Select-filter.selected.value" to UserFilter.PROFILE_APPROVAL_PENDING.name
            ))
        }
        else if (filter == UserFilter.PROFILE_REJECTED.name) {
            global.Object.assign(expected, json(
                "Select-filter.selected.title" to "Завернутые",
                "Select-filter.selected.value" to UserFilter.PROFILE_REJECTED.name
            ))
        }
        else if (filter == UserFilter.BANNED.name) {
            global.Object.assign(expected, json(
                "Select-filter.selected.title" to "Забаненые",
                "Select-filter.selected.value" to UserFilter.BANNED.name
            ))
        }
        else {
            Shitus.raise("Weird filter to ${filter}")
        }

        if (ordering == Ordering.ASC.name) {
            global.Object.assign(expected, json(
                "Select-ordering.selected.title" to "Сначала старые",
                "Select-ordering.selected.value" to Ordering.ASC.name
            ))
        }
        else if (ordering == Ordering.DESC.name) {
            global.Object.assign(expected, json(
                "Select-ordering.selected.title" to "Сначала новые",
                "Select-ordering.selected.value" to Ordering.DESC.name
            ))
        }
        else {
            Shitus.raise("Weird ordering to ${ordering}")
        }
    }
}

class TestActionBuilder(val items: MutableList<TestInstruction>) {
    fun setValue(shame: String, value: String) {
        items.add(TestInstruction.SetValue(shame, value))
    }

    fun setValue(shame: String, value: Boolean) {
        items.add(TestInstruction.SetCheckbox(shame, value))
    }

    fun click(shame: String, timestamp: String) {
        items.add(TestInstruction.Click(shame, timestamp))
    }

    fun keyDown(shame: String, keyCode: Int) {
        items.add(TestInstruction.KeyDown("Input-search", 13))
    }
}

class TestStateBuilder(val items: MutableList<TestInstruction>) {
    fun assertGen(tag: String, expectedExtender: ExpectationExtender? = null) {
        items.add(TestInstruction.AssertGenerated(tag, "---generated-shit---", expectedExtender))
    }
}

class TestSectionBuilder(val items: MutableList<TestInstruction>) {

    fun action(long: String, build: TestActionBuilder.() -> Unit) {
        items.add(TestInstruction.Step("action", long))

        val builder = TestActionBuilder(items)
        builder.build()
    }

    fun state(long: String, build: TestStateBuilder.() -> Unit) {
        items.add(TestInstruction.Step("state", long))

        val builder = TestStateBuilder(items)
        builder.build()
    }

    operator fun Iterable<TestInstruction>.unaryPlus() {
        for (item in this) add(item)
    }

    operator fun TestInstruction.unaryMinus() {
        add(this)
    }

    fun add(item: TestInstruction) {
        items.add(item)
    }

}

class PieceOfTestBuilder(val items: MutableList<TestInstruction>) {
    fun section(long: String, build: TestSectionBuilder.() -> Unit) {
        items.add(TestInstruction.BeginSection(long))

        val builder = TestSectionBuilder(items)
        builder.build()

        items.add(TestInstruction.EndSection())
    }
}


fun buildPieceOfTest(build: PieceOfTestBuilder.() -> Unit): Iterable<TestInstruction> {
    val items = mutableListOf<TestInstruction>()
    val builder = PieceOfTestBuilder(items)
    builder.build()

    return items
}



