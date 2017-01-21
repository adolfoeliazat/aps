package aps.front

import aps.*
import into.mochka.*
import kotlin.text.js.RegExp

fun runAllUnitTests() {
    runMochka {
//        describeConcurrentModificationException()
        describeUtils()
    }
}

private fun runMochka.describeUtils() {
    describe("Utils") {
        test(::formatFileSizeApprox.name) {
            assertEquals("244 Б", formatFileSizeApprox(Language.UA, 244))
            assertEquals("243 КБ", formatFileSizeApprox(Language.UA, 248_987))
            assertEquals("12,5 МБ", formatFileSizeApprox(Language.UA, 13_127_419))
            assertEquals("2 МБ", formatFileSizeApprox(Language.UA, 2_097_152))
            assertEquals("2 МБ", formatFileSizeApprox(Language.UA, 2_199_551))
            assertEquals("2,1 МБ", formatFileSizeApprox(Language.UA, 2_199_552))
        }

        test(::windowLocationHrefToClientURL.name) {
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012"))
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012/"))
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012/sign-in.html"))
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012/sign-in.html?pizda=hairy"))
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012/foo/bar/baz/shit.html"))
            assertEquals("http://aps-ua-customer.local:3012", windowLocationHrefToClientURL("http://aps-ua-customer.local:3012/foo/bar/baz/shit.html?pizda=hairy"))

            assertEquals("https://staticshit.github.io/apsua", windowLocationHrefToClientURL("https://staticshit.github.io/apsua"))
            assertEquals("https://staticshit.github.io/apsua", windowLocationHrefToClientURL("https://staticshit.github.io/apsua/"))
            assertEquals("https://staticshit.github.io/apsua", windowLocationHrefToClientURL("https://staticshit.github.io/apsua/sign-in.html"))
            assertEquals("https://staticshit.github.io/apsua", windowLocationHrefToClientURL("https://staticshit.github.io/apsua/foo/bar/baz/shit.html"))
        }

        test(::highlightedHTML.name) {
            val expected = dedent("""
                26-летний князь Лев Николаевич <span style='background-color: #ffc107;'>Мышкин</span> возвращается из санатория \ в Швейцарии, \{где\} провёл несколько лет, лечась от эпилепсии. Князь предстаёт человеком искренним и невинным, хотя и прилично разбирающимся в отношениях между людьми. Он едет в Россию к единственным оставшимся у него родственникам — семье Епанчиных. В поезде он знакомится с молодым купцом Парфёном Рогожиным и отставным чиновником Лебедевым, которым бесхитростно рассказывает свою историю. В ответ он узнаёт подробности жизни Рогожина, который влюблён в <span style='background-color: #ffc107;'>Настасью</span> Филипповну, бывшую содержанку богатого дворянина Афанасия Ивановича Тоцкого.

                В доме Епанчиных выясняется, что <span style='background-color: #ffc107;'>Настасья</span> Филипповна там хорошо известна. Есть план выдать её за протеже генерала Епанчина Гаврилу Ардалионовича Иволгина, человека амбициозного, но посредственного. Князь <span style='background-color: #ffc107;'>Мышкин</span> знакомится со всеми основными персонажами повествования. Это дочери Епанчиных — Александра, Аделаида и Аглая, на которых он производит благоприятное впечатление, оставаясь объектом их немного насмешливого внимания. Это генеральша Лизавета Прокофьевна Епанчина, которая находится в постоянном волнении из-за того, что её муж общается с <span style='background-color: #ffc107;'>Настасьей</span> Филипповной, имеющей репутацию падшей. Это Ганя Иволгин, который очень страдает из-за предстоящей ему роли мужа <span style='background-color: #ffc107;'>Настасьи</span> Филипповны, хотя ради денег готов на всё, и не может решиться на развитие своих пока очень слабых отношений с Аглаей. Князь <span style='background-color: #ffc107;'>Мышкин</span> довольно простодушно рассказывает генеральше и сёстрам Епанчиным о том, что он узнал о <span style='background-color: #ffc107;'>Настасье</span> Филипповне от Рогожина, а также поражает их своим повествованием о воспоминаниях и чувствах своего знакомого, который был приговорён к смертной казни, но в последний момент был помилован.
            """)
            val actual = highlightedHTML(
                dedent("""
                    26-летний князь Лев Николаевич Мышкин возвращается из санатория \ в Швейцарии, \{где\} провёл несколько лет, лечась от эпилепсии. Князь предстаёт человеком искренним и невинным, хотя и прилично разбирающимся в отношениях между людьми. Он едет в Россию к единственным оставшимся у него родственникам — семье Епанчиных. В поезде он знакомится с молодым купцом Парфёном Рогожиным и отставным чиновником Лебедевым, которым бесхитростно рассказывает свою историю. В ответ он узнаёт подробности жизни Рогожина, который влюблён в Настасью Филипповну, бывшую содержанку богатого дворянина Афанасия Ивановича Тоцкого.

                    В доме Епанчиных выясняется, что Настасья Филипповна там хорошо известна. Есть план выдать её за протеже генерала Епанчина Гаврилу Ардалионовича Иволгина, человека амбициозного, но посредственного. Князь Мышкин знакомится со всеми основными персонажами повествования. Это дочери Епанчиных — Александра, Аделаида и Аглая, на которых он производит благоприятное впечатление, оставаясь объектом их немного насмешливого внимания. Это генеральша Лизавета Прокофьевна Епанчина, которая находится в постоянном волнении из-за того, что её муж общается с Настасьей Филипповной, имеющей репутацию падшей. Это Ганя Иволгин, который очень страдает из-за предстоящей ему роли мужа Настасьи Филипповны, хотя ради денег готов на всё, и не может решиться на развитие своих пока очень слабых отношений с Аглаей. Князь Мышкин довольно простодушно рассказывает генеральше и сёстрам Епанчиным о том, что он узнал о Настасье Филипповне от Рогожина, а также поражает их своим повествованием о воспоминаниях и чувствах своего знакомого, который был приговорён к смертной казни, но в последний момент был помилован.
                """),
                listOf(31..37 - 1, 814..820 - 1, 1411..1417 - 1,
                       523..531 - 1, 643..651 - 1, 1156..1165 - 1, 1278..1286 - 1, 1505..1513 - 1),
                Color.AMBER_500)
            if (false) {
                for (i in 0 until expected.length) {
                    if (expected[i] != actual[i]) {
                        clog(i,
                             "Expected: [${expected[i]}] (${expected[i].toInt()})",
                             "Actual: [${actual[i]}] (${actual[i].toInt()})")
                        process.exit(1)
                    }
                }
            }

            assertEquals(expected, actual)
        }

        describe("dejsonize") {
            ArrayList<String>()
            test("1") {
                val json = """{"${'$'}${'$'}${'$'}class":"aps.VisualShitCapturedRequest","id":"fucking id","shots":[{"${'$'}${'$'}${'$'}class":"aps.VisualShitCapturedRequest${'$'}Shot","dataURL":"fucking data 1","windowScrollY":10},{"${'$'}${'$'}${'$'}class":"aps.VisualShitCapturedRequest${'$'}Shot","dataURL":"fucking data 2","windowScrollY":20}]}"""
                val obj = dejsonize<VisualShitCapturedRequest>(json)
                if (false) {
                    clog("id", obj.id)
                    for ((i, shot) in obj.shots.withIndex()) {
                        clog("shot $i:")
                        clog("  dataURL", shot.dataURL)
                        clog("  windowScrollY", shot.windowScrollY)
                    }
                }
                assertEqualsStructurally(
                    expected = VisualShitCapturedRequest()-{o->
                        o.id = "fucking id"
                        o.shots = listOf(
                            BrowserShot()-{o->
                                o.dataURL = "fucking data 1"
                                o.windowScrollY = 101.0
                            },
                            BrowserShot()-{o->
                                o.dataURL = "fucking data 2"
                                o.windowScrollY = 20.0
                            }
                        )
                    },
                    actual = obj
                )
            }
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

