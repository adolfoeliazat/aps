import kotlin.browser.document
import kotlin.browser.window

/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

@native interface IReactElement {
}

@native interface IKotlinShit {
    fun ignite(global: dynamic, jshit: dynamic)
}

var jshit: dynamic = null

object KotlinShit : IKotlinShit {
    override fun ignite(global: dynamic, _jshit: dynamic) {
        println("----- Igniting front Kotlin shit -----")
        jshit = _jshit

        jshit.art.openTestPassedPane = { def: dynamic ->
            val scenario = def.scenario

            val testPassedPane = jshit.statefulElement(json("ctor" to { update: dynamic ->
                var scenarioName = scenario.name
                val links = mutableListOf<IReactElement>()

                val m = global.RegExp("\\s+([0-9a-z]{8})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{4})-([0-9a-z]{12})$").exec(scenarioName)
                if (m != undefined) { // TODO
                    scenarioName = scenarioName.slice(0, m.index)
                    links.add(jshit.OpenSourceCodeLink(json("where" to json("\$tag" to m[0].trim()), "style" to json("color" to jshit.WHITE))))
                }
                if (jshit.art.actionPlaceholderTag != undefined) {
                    links.add(jshit.marginateLeft(10, jshit.OpenSourceCodeLink(json("where" to json("\$tag" to jshit.art.actionPlaceholderTag), "style" to json("color" to jshit.WHITE)))))
                }

                val uq = jshit.getURLQueryBeforeRunningTest()
                if (!uq.scrollToBottom || uq.scrollToBottom === "yes" || uq.scrollToBottom === "success") {
                    window.requestAnimationFrame { document.body?.scrollTop = 99999 }
                }

                json(
                    "render" to {
                        when {
                            scenarioName == undefined -> null

                            else -> jshit.diva(json(
                                "noStateContributions" to true,
                                "style" to json(
                                    "backgroundColor" to jshit.GREEN_700, "color" to jshit.WHITE,
                                    "marginTop" to 10, "padding" to "10px 10px", "textAlign" to "center", "fontWeight" to "bold")),

                                jshit.diva(json("style" to json("paddingBottom" to 10)),
                                    scenarioName,
                                    diva(json("style" to json("display" to "flex", "justifyContent" to "center")), *links.toTypedArray())
                                ),

                                jshit.diva(json("style" to json("background" to jshit.WHITE, "color" to jshit.BLACK_BOOT, "fontWeight" to "normal", "textAlign" to "left", "padding" to 5)),
                                    jshit.art.renderStepDescriptions())
                            )
                        }
                    })
            }))

            jshit.debugPanes.set(json(
                "name" to "openTestPassedPane",
                "parentJqel" to jshit.byid("underFooter"),
                "element" to jshit.spana(json(), testPassedPane.element)))
        }
    }

    fun diva(vararg args: dynamic) {
        val shit = jshit
        return js("shit.diva.apply(null, args)")
    }

    private fun fuckingDiv(): dynamic {
        return jshit.diva(json(), "Fucking Divius")
    }

}


