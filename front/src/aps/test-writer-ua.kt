/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

fun dynamicAsString(x: dynamic): String {
    return x
}

@native interface StackAsError

fun captureStackAsError(): StackAsError {
    return js("Error()")
}

fun jsonTestItem(stack: StackAsError, firstStackLine: Int, vararg pairs: Pair<String, Any?>): TestItem {
    return object : TestItem {
            override fun toJSObject(): dynamic {
                val obj: dynamic = json(*pairs)
                obj.`$definitionStack` = promiseDefinitionStack(stack, firstStackLine)

//                obj.`$definitionStack` = {"__async"
//                    __await<dynamic>(jshit.delay(1000))
//                    jsArrayOf(json("loc" to "foo"), json("loc" to "bar"))
//                }
                return obj
            }
        }
}

fun jsonTestItem(vararg pairs: Pair<String, Any?>): TestItem {
    return jsonTestItem(captureStackAsError(), 2, *pairs)
}

fun selectBrowser(clientKind: String, browserName: String, stateDescription: String): Iterable<TestItem> {
    val res = mutableListOf(
        jsonTestItem("step" to json("kind" to "navigation", "long" to "Trying to open dashboard page as " + browserName)),
        jsonTestItem("do" to json(
            "action" to {"__async"
                global.CLIENT_KIND = clientKind
                global.sim.selectBrowser(browserName)
                __await<dynamic>(global.sim.navigate("dashboard.html"))
            }
        )),

        jsonTestItem("step" to json("kind" to "state", "long" to stateDescription))
        // s{pausePoint: {title: stateDescription, theme: "blue"}},
    )

    if (clientKind == "writer") {
        res.addAll(listOf(
            jsonTestItem("assert" to json("\$tag" to "42816503-1dc0-4e43-aaa4-a8b11a680501", "expected" to json(
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
            )))))
    } else if (clientKind == "customer") {
        res.addAll(listOf(
            jsonTestItem("assert" to json("\$tag" to "4d7bfd1d-d5cd-4b64-a069-a99f85f934da", "expected" to json(
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
            )))))
    } else {
        throw js.Error("WTF is the clientKind")
    }

    return res
}

fun signIn(email: String, password: String): Iterable<TestItem> {
    return listOf(
        jsonTestItem("setValue" to json("shame" to "TextField-email.Input", "value" to email)),
        jsonTestItem("setValue" to json("shame" to "TextField-password.Input", "value" to password)),
        jsonTestItem("click" to json("shame" to "button-primary"))
    )
}

fun jsFacing_test_UA_Writer_SignUp_1_vovchok1(): JSArray {
    return buildPieceOfTest {
        section("Marko Vovchok signs up") {

            + selectBrowser("writer", "vovchok1", "Marko Vovchok, an eager writer wannabe, comes to our site")

            action("Trying to sign in (to non-existing account)") {
                + signIn("vovchok@test.shit.ua", "something")
            }
            state("Of course it failed") {
                assertGen("1a543472-b429-4add-88e8-799598607ad3")
            }

            action("Clicking 'Sign Up' link") {
                setValue("TextField-email.Input", "vovchok@test.shit.ua")
                setValue("TextField-password.Input", "something")
                click("link-createAccount", "2016/08/12 20:40:58")
            }
            state("Got registration form") {
                assertGen("877a3f2f-ad7d-41c7-af4b-9665526fc27f")
            }

            section("Submitting sign-up form") {
                action("Trying to submit empty form") {
                    click("button-primary", "2016/08/14 12:55:19")
                }
                state("Got a bunch of errors") {
                    assertGen("9b0cad6b-c396-4b8a-b731-57905900a80a")
                }

                action("Agreeing terms") {
                    setValue("AgreeTermsField.Checkbox", true)
                    click("button-primary", "2016/08/14 14:22:04")
                }
                state("-1 error message") {
                    assertGen("7dbefeef-84f6-47e2-8635-d67ddcbb153d")
                }

                action("Entering email") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    click("button-primary", "2016/08/14 14:23:51")
                }
                state("-1 error message") {
                    assertGen("ac403942-31f7-45a3-b42c-e37f6c1b83a2")
                }

                action("Entering first name") {
                    setValue("TextField-firstName.Input", "Марко")
                    click("button-primary", "2016/08/14 14:25:32")
                }
                state("-1 error message") {
                    assertGen("f866c286-58f4-4785-a2da-bad213bd4567")
                }

                action("Entering last name") {
                    setValue("TextField-lastName.Input", "Вовчок")
                    - jsonTestItem("do" to json(
                        "action" to {"__async"
                            __await<dynamic>(global.drpc(json("fun" to "danger_imposeNextGeneratedPassword", "password" to "fucking-big-generated-secret")))
                        }
                    ))
                    click("button-primary", "2016/08/14 17:48:51")
                }
                state("Sign-in form with message that account is created. Got email with password") {
                    assertGen("0b7e96bf-4f42-41dc-b658-7b60e863356e")
                }

                - jsonTestItem("do" to json(
                    "action" to {"__async"
                        __await<dynamic>(global.drpc(json("fun" to "danger_clearSentEmails")))
                    }
                ))
            }

            section("Submitting sign-in form") {
                action("First try with wrong password") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("TextField-password.Input", "dead-wrong-shit")
                    click("button-primary", "2016/08/15 19:26:34")
                }
                state("No way") {
                    assertGen("51815ec8-62e9-4c25-aad5-fa97f30d6cff")
                }

                action("Now with password from received email") {
                    setValue("TextField-email.Input", "vovchok@test.shit.ua")
                    setValue("TextField-password.Input", "fucking-big-generated-secret")
                    click("button-primary", "2016/08/15 19:48:57")
                }
                state("Got page asking to fill profile") {
                    assertGen("00997d56-f00a-4b6a-bffd-595ba4c23806")
                }
            }

            section("Submitting profile") {
                action("Trying to send empty profile") {
                    setValue("TextField-phone.Input", "")
                    setValue("TextField-aboutMe.Input", "")
                    click("button-primary", "2016/08/15 20:12:37")
                }
                state("Bunch of errors") {
                    assertGen("4fc3f3d3-2806-4c99-9769-6d046cd7a985")
                }

                action("Entering phone: some junk") {
                    setValue("TextField-phone.Input", "qwerty")
                    click("button-primary", "2016/08/15 20:16:20")
                }
                state("No way") {
                    assertGen("243f5834-48a4-496b-8770-371ac0c94b19")
                }

                action("Entering phone: too long") {
                    setValue("TextField-phone.Input", "012345678901234567890")
                    click("button-primary", "2016/08/16 11:56:01")
                }
                state("No way") {
                    assertGen("a07d90e4-60a6-45c1-a5c6-cd217a01a933")
                }

                action("Entering phone: too few digits") {
                    setValue("TextField-phone.Input", "--3234--++--")
                    click("button-primary", "2016/08/16 11:56:05")
                }
                state("No way") {
                    assertGen("3f290096-9162-419b-835d-ec2b02f8f5ca")
                }

                action("Entering phone: something sane") {
                    setValue("TextField-phone.Input", "+38 (068) 9110032")
                    click("button-primary", "2016/08/16 12:02:30")
                }
                state("-1 error") {
                    assertGen("d072d3c4-38c9-481a-9fec-0ecd9c5601a2")
                }

                action("Entering about: too long") {
                    setValue("TextField-aboutMe.Input", dynamicAsString(global.apsCommon.LONG_SHIT_301))
                    click("button-primary", "2016/08/16 14:01:33")
                }
                state("TODO State description") {
                    assertGen("e3c1c929-27af-4742-850c-1c1b7cb2ff51")
                }

                action("Entering aboutMe: something sane") {
                    setValue("TextField-aboutMe.Input", "I am a fucking bitch. No, really. Wanna have one for the team?")
                    click("button-primary", "2016/08/16 14:55:45")
                }
                state("TODO State description") {
                    assertGen("9219ded3-54e4-4d6b-bf1b-63615cf8a56e")
                }

//                s{actionPlaceholder: {$tag: '6bcd180a-9701-4e84-babe-99253f49e44b'}},
            }
        }

    }
}


fun jsFacing_pieceOfTest100(): JSArray {
    return buildPieceOfTest {
        section("Submitting sign-in form") {
            action("First try with wrong password") {
                setValue("TextField-email.Input", "vovchok@test.shit.ua")
                setValue("TextField-password.Input", "dead-wrong-shit")
                click("button-primary", "2016/08/15 19:26:34")
            }
            state("No way") {
                assertGen("51815ec8-62e9-4c25-aad5-fa97f30d6cff")
            }

            action("Now with password from received email") {
                setValue("TextField-email.Input", "vovchok@test.shit.ua")
                setValue("TextField-password.Input", "fucking-big-generated-secret")
                click("button-primary", "2016/08/15 19:48:57")
            }
            state("Got page asking to fill profile") {
                assertGen("00997d56-f00a-4b6a-bffd-595ba4c23806")
            }
        }
    }
}

class TestActionBuilder(val items: MutableList<TestItem>) {

    fun setValue(shame: String, value: String) {
        items.add(jsonTestItem("setValue" to json("shame" to shame, "value" to value)))
    }

    fun setValue(shame: String, value: Boolean) {
        items.add(jsonTestItem("setValue" to json("shame" to shame, "value" to value)))
    }

    fun click(shame: String, timestamp: String) {
        items.add(jsonTestItem("click" to json("shame" to shame, "timestamp" to timestamp)))
    }

}

class TestStateBuilder(val items: MutableList<TestItem>) {
    fun assertGen(tag: String) {
        items.add(jsonTestItem("assert" to json("\$tag" to tag, "expected" to "---generated-shit---")))
    }
}

class TestSectionBuilder(val items: MutableList<TestItem>) {

    fun action(long: String, build: TestActionBuilder.() -> Unit) {
        items.add(jsonTestItem("step" to json("kind" to "action", "long" to long)))

        val builder = TestActionBuilder(items)
        builder.build()
    }

    fun state(long: String, build: TestStateBuilder.() -> Unit) {
        items.add(jsonTestItem("step" to json("kind" to "state", "long" to long)))

        val builder = TestStateBuilder(items)
        builder.build()
    }

    operator fun Iterable<TestItem>.unaryPlus() {
        for (item in this) add(item)
    }

    operator fun TestItem.unaryMinus() {
        add(this)
    }

    fun add(item: TestItem) {
        items.add(item)
    }

}

class PieceOfTestBuilder(val items: MutableList<TestItem>) {
    fun section(long: String, build: TestSectionBuilder.() -> Unit) {
        items.add(jsonTestItem("beginSection" to json("long" to long)))

        val builder = TestSectionBuilder(items)
        builder.build()

        items.add(jsonTestItem("endSection" to json()))
    }
}

@native interface JSArray

fun <T> Iterable<T>.toJSArray(): JSArray {
    val res = js("[]")
    this.forEach { res.push(it) }
    return res
}

interface TestItem {
    fun toJSObject(): dynamic
}

fun buildPieceOfTest(build: PieceOfTestBuilder.() -> Unit): JSArray {
    val items = mutableListOf<TestItem>()
    val builder = PieceOfTestBuilder(items)
    builder.build()

    return items.map { it.toJSObject() }.toJSArray()
}



