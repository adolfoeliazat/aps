/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps

fun jsFacing_testScenarios(sim: dynamic) {
    return global.Object.assign(js("({})"),
        jsFacing_tests_UA_Writer(sim))
}

class TestCommon(val sim: dynamic) {
    val LONG_SHIT_301 = makeLongShit(301)

    fun makeLongShit(len: Int): String {
        val bunchOfShit = buildString {
            while (length < len) {
                append("iamlongshit")
            }
        }

        return bunchOfShit.substring(0, len)
    }

}


