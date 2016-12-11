package aps.front

import aps.*
import into.mochka.*

fun runAllUnitTests() {
    runMochka {
//        describeConcurrentModificationException()
        describeUtils()
    }
}

private fun runMochka.describeUtils() {
    describe("Utils") {
        test("formatFileSizeApprox") {
            assertEquals("244 Б", formatFileSizeApprox(Language.UA, 244))
            assertEquals("243 КБ", formatFileSizeApprox(Language.UA, 248_987))
            assertEquals("12,5 МБ", formatFileSizeApprox(Language.UA, 13_127_419))
            assertEquals("2 МБ", formatFileSizeApprox(Language.UA, 2_097_152))
            assertEquals("2 МБ", formatFileSizeApprox(Language.UA, 2_199_551))
            assertEquals("2,1 МБ", formatFileSizeApprox(Language.UA, 2_199_552))
        }
    }
}


private fun runMochka.describeConcurrentModificationException() {
    describe("ConcurrentModificationException") {
        describe("mutableListOf") {
            test("1") {
                // val list = kotlin.collections.mutableListOf("foo", "bar", "baz")
                val list = mutableListOf("fuck", "shit", "bitch")
                val it1 = list.iterator()
                assertEquals("fuck", it1.next())
                list.removeAt(0)
                val it2 = list.iterator()
                assertEquals("shit", it2.next())
                assert(assertFails {it1.next()} is ConcurrentModificationException)
            }
        }

        describe("mutableMapOf") {
            test("1") {
                // val map = kotlin.collections.mutableMapOf("fuck" to "big", "shit" to "little")
                val map = mutableMapOf("fuck" to "big", "shit" to "little")
                val it1 = map.iterator()
                val pair1 = it1.next().toPair()
                assert(pair1 == ("fuck" to "big") || pair1 == ("shit" to "little"))
                map.remove("fuck")
                val it2 = map.iterator()
                assertEquals("shit" to "little", it2.next().toPair())
                assertFalse(it2.hasNext())
                assert(assertFails {it1.next()} is ConcurrentModificationException)
            }
        }
    }
}

