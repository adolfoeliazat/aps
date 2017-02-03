@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Math
import kotlin.js.json
import kotlin.properties.Delegates.notNull

val visualShitCaptured = ResolvableShit<VisualShitCapturedMessageData>()
private var shitID by notNull<String>()

external interface VisualShitCapturedMessageData {
    val dataURL: String
}

fun visualShitCaptured(data: VisualShitCapturedMessageData) {
    visualShitCaptured.resolve(data)
}

fun captureVisualShit(id: String): Promisoid<VisualShitCapturedRequest.Response> = async {
    await(send(MoveMouseAwayFromPageRequest()))
    old_debugPanes.hideAll()
    try {
        check(window.devicePixelRatio == 1.25 && window.innerWidth == 1008) {
            "Visual testing is designed for window.devicePixelRatio == 1.25 && window.innerWidth == 1008. " +
            "Otherwise there are tiny little differences in rendering of, for example, rounded corners, etc."
        }

        val my = object {
            var documentHeight by notNull<Double>()
            var documentHeightPhysicalDouble by notNull<Double>()
            var isHeightGood by notNull<Boolean>()
        }

        fun determineHeight(original: Boolean = false) {
            my.documentHeight = document.documentElement!!.getBoundingClientRect().height
            my.documentHeightPhysicalDouble = my.documentHeight.toPhysicalPixelsDouble()
            my.isHeightGood = Math.abs(my.documentHeightPhysicalDouble - my.documentHeightPhysicalDouble) < 0.001
            clog("${ifOrEmpty(original){"Original: "}}documentHeight = ${my.documentHeight}; documentHeightPhysicalDouble = ${my.documentHeightPhysicalDouble}; isHeightGood = ${my.isHeightGood}")
        }

        determineHeight(original = true)
        if (!my.isHeightGood) {
            val shouldBePhysical = Math.ceil(my.documentHeightPhysicalDouble)
            val deltaPhysical = shouldBePhysical - my.documentHeightPhysicalDouble
            val deltaPixels = deltaPhysical / window.devicePixelRatio // ph = px * ratio  -->  px = ph / ratio
            clog("shouldBePhysical = $shouldBePhysical; deltaPhysical = $deltaPhysical; deltaPixels = $deltaPixels")
            jqbody.css("margin-bottom", "${deltaPixels}px")
            determineHeight()
        }
        check(my.isHeightGood) {"Fucky document height"}

        val documentHeightPhysical: Int = my.documentHeight.toPhysicalPixels()
        val windowHeight: Double = window.asDynamic().innerHeight
        val windowHeightPhysical: Int = windowHeight.toPhysicalPixels()
        val topNavbarHeightPhysical: Int = const.topNavbarHeight.toPhysicalPixels()
        val scrollStepPhysical: Int = windowHeightPhysical - topNavbarHeightPhysical
        clog("documentHeight = ${my.documentHeight}; documentHeightPhysicalDouble = ${my.documentHeightPhysicalDouble}; documentHeightPhysical = $documentHeightPhysical; windowHeight = $windowHeight; windowHeightPhysical = $windowHeightPhysical; topNavbarHeightPhysical = $topNavbarHeightPhysical; scrollStepPhysical = $scrollStepPhysical")

        byid(const.elementID.dynamicFooter).css("display", "none")
        val origScrollY = window.scrollY

        val drawPurpleLines = false
        if (drawPurpleLines) {
            jqbody.append("<div id='${const.elementID.cutLineContainer}'></div>")
            var yPhysical = topNavbarHeightPhysical
            while (yPhysical < documentHeightPhysical) {
                byid(const.elementID.cutLineContainer).append(
                    "<div class='${css.test.cutLine}' style='top: ${yPhysical.toLayoutPixels()}px; height: ${1.0 / window.devicePixelRatio}px'></div>")
                byid(const.elementID.cutLineContainer).append(
                    "<div class='${css.test.cutLine}' style='top: ${(yPhysical + scrollStepPhysical - 1).toLayoutPixels()}px; height: ${1.0 / window.devicePixelRatio}px'></div>")
                yPhysical += scrollStepPhysical
            }
        }
        // await(tillHourPasses())

        val shots = mutableListOf<BrowserShot>()

        freezeShitForCapturing()
        while (true) {
            if (shots.size == 10) bitch("Too many fucking chunks to shoot")

            val requestedYPhysical = shots.size * scrollStepPhysical
            val requestedY = requestedYPhysical.toLayoutPixels()
            if (!isModalShown()) {
                window.scroll(0.0, requestedY)
            }
            await(delay(100)) // Fucking Chrome...
            clog("Shooting at $requestedY (window.scrollY = ${window.scrollY}; requestedYPhysical = $requestedYPhysical)")

            visualShitCaptured.reset()
            shitID = id
            window.postMessage(json("type" to "captureVisualShit"), "*")
            val data = await(visualShitCaptured.promise)
            shots += BrowserShot()-{o->
                o.dataURL = data.dataURL
                o.windowScrollY = window.scrollY
                o.windowScrollYPhysical = window.scrollY.toPhysicalPixels()
            }

            val oldY = window.scrollY
            window.scroll(0.0, oldY + 1)
            val dy = Math.abs(oldY - window.scrollY)
            clog("oldY = $oldY; window.scrollY = ${window.scrollY}; dy = $dy")

            if (dy < 0.001) break
            if (isModalShown()) break
        }
        unfreezeShitAfterCapturing()

        val res = await(send(VisualShitCapturedRequest()-{o->
            o.id = shitID
            o.shots = shots
            o.devicePixelRatio = window.devicePixelRatio
            o.headerHeight = const.topNavbarHeight
            o.contentWidth = jq("#topNavbarContainer > nav > .container").outerWidth()
            o.contentLeft = jq("#topNavbarContainer > nav > .container").offset().left.toDouble()
            o.documentHeightPhysical = documentHeightPhysical
            o.modal = isModalShown()
        }))
        clog("Sent captured shit to backend")

        jqbody.css("margin-bottom", "0px")
        byid(const.elementID.cutLineContainer).remove()
        window.scroll(0.0, origScrollY)
        byid(const.elementID.dynamicFooter).css("display", "")

        res
    } finally {
        old_debugPanes.showAll()
        await(send(ReturnMouseWhereItWasRequest()))
    }
}

fun freezeShitForCapturing() {
    progressTickerKeyframe100RuleStyle.opacity = "1"
    testArtifactClasses.forEach {jq(".$it").hide()}
}

fun unfreezeShitAfterCapturing() {
    progressTickerKeyframe100RuleStyle.opacity = "0"
    testArtifactClasses.forEach {jq(".$it").show()}
}












