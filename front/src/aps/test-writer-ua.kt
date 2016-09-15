/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

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
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("setValue" to json("shame" to shame, "value" to value))
            }
        })
    }

    fun click(shame: String, timestamp: String) {
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("click" to json("shame" to shame, "timestamp" to timestamp))
            }
        })
    }

}

class TestStateBuilder(val items: MutableList<TestItem>) {
    fun assertGen(tag: String) {
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("assert" to json("\$tag" to tag, "expected" to "---generated-shit---"))
            }
        })
    }
}

class TestSectionBuilder(val items: MutableList<TestItem>) {

    fun action(long: String, build: TestActionBuilder.() -> Unit) {
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("step" to json("kind" to "action", "long" to long))
            }
        })

        val builder = TestActionBuilder(items)
        builder.build()
    }

    fun state(long: String, build: TestStateBuilder.() -> Unit) {
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("step" to json("kind" to "state", "long" to long))
            }
        })

        val builder = TestStateBuilder(items)
        builder.build()
    }

}

class PieceOfTestBuilder(val items: MutableList<TestItem>) {
    fun section(long: String, build: TestSectionBuilder.() -> Unit) {
        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("beginSection" to json("long" to long))
            }
        })

        val builder = TestSectionBuilder(items)
        builder.build()

        items.add(object : TestItem {
            override fun toJSObject(): dynamic {
                return json("endSection" to json())
            }
        })
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


//    return jsArrayOf(
//        json("beginSection" to json("long" to t("Submitting sign-in form"))),
//            json("step" to json("kind" to "action", "long" to t("First try with wrong password"))),
//            json("setValue" to json("shame" to "TextField-email.Input", "value" to "vovchok@test.shit.ua")),
//            json("setValue" to json("shame" to "TextField-password.Input", "value" to "dead-wrong-shit")),
//            json("click" to json("shame" to "button-primary", "timestamp" to "2016/08/15 19:26:34")),
//            json("step" to json("kind" to "state", "long" to t("No way"))),
//            json("assert" to json("\$tag" to "51815ec8-62e9-4c25-aad5-fa97f30d6cff", "expected" to "---generated-shit---")),
//
//            json("step" to json("kind" to "action", "long" to t("Now with password from received email"))),
//            json("setValue" to json("shame" to "TextField-email.Input", "value" to "vovchok@test.shit.ua")),
//            json("setValue" to json("shame" to "TextField-password.Input", "value" to "fucking-big-generated-secret")),
//            json("click" to json("shame" to "button-primary", "timestamp" to "2016/08/15 19:48:57")),
//            json("step" to json("kind" to "state", "long" to t("Got page asking to fill profile"))),
//            json("assert" to json("\$tag" to "00997d56-f00a-4b6a-bffd-595ba4c23806", "expected" to "---generated-shit---")),
//        json("endSection" to json())
//    )


