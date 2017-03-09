@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import org.w3c.dom.HTMLElement
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

suspend fun captureVisualShit(id: String) = await(captureVisualShitPromise(id))

fun captureVisualShitPromise(id: String): Promisoid<VisualShitCapturedRequest.Response> = async {
    await(send(MoveMouseAwayFromPageRequest()))
    old_debugPanes.hideAll()
    try {
        check(window.devicePixelRatio == 1.25 && window.innerWidth == 1008) {
            "Visual testing is designed for window.devicePixelRatio == 1.25 && window.innerWidth == 1008. " +
            "Otherwise there are tiny little differences in rendering of, for example, rounded corners, etc."
        }

        val my = object {
            var containerHeight by notNull<Double>()
            var containerHeightPhysicalDouble by notNull<Double>()
            var isHeightGood by notNull<Boolean>()
        }

        val isModal: Boolean
        val containerForScrolling: HTMLElement
        val getContainerHeight: () -> Double

        val jqModalFade = jq(".modal.fade")
        if (jqModalFade.length == 0) {
            isModal = false
        } else {
            check (jqModalFade.length == 1) {"Too many .modal.fades"}
            isModal = true
        }
        if (isModal) {
            containerForScrolling = bang(jqModalFade[0])
            val modalMargin = 30
            getContainerHeight = {bang(jq(".modal-dialog")[0]).getBoundingClientRect().height + 2 * modalMargin}
        } else {
            containerForScrolling = bang(document.body)
            getContainerHeight = {bang(document.documentElement).getBoundingClientRect().height}
        }

        fun determineHeight(original: Boolean = false) {
            my.containerHeight = getContainerHeight()
            my.containerHeightPhysicalDouble = my.containerHeight.toPhysicalPixelsDouble()
            my.isHeightGood = Math.abs(my.containerHeightPhysicalDouble - my.containerHeightPhysicalDouble) < 0.001
            clog("${ifOrEmpty(original){"Original: "}}containerHeight = ${my.containerHeight}; containerHeightPhysicalDouble = ${my.containerHeightPhysicalDouble}; isHeightGood = ${my.isHeightGood}")
        }

        determineHeight(original = true)
        if (!my.isHeightGood) {
            val shouldBePhysical = Math.ceil(my.containerHeightPhysicalDouble)
            val deltaPhysical = shouldBePhysical - my.containerHeightPhysicalDouble
            val deltaPixels = deltaPhysical / window.devicePixelRatio // ph = px * ratio  -->  px = ph / ratio
            clog("shouldBePhysical = $shouldBePhysical; deltaPhysical = $deltaPhysical; deltaPixels = $deltaPixels")
            jqbody.css("margin-bottom", "${deltaPixels}px")
            determineHeight()
        }
        check(my.isHeightGood) {"Fucky document height"}

        val containerHeightPhysical: Int = my.containerHeight.toPhysicalPixels()
        val windowHeight: Double = window.asDynamic().innerHeight
        val windowHeightPhysical: Int = windowHeight.toPhysicalPixels()
        val topNavbarHeightPhysical: Int = const.topNavbarHeight.toPhysicalPixels()
        var scrollStepPhysical: Int = windowHeightPhysical
        if (!isModal) {
            scrollStepPhysical -= topNavbarHeightPhysical
        }
        clog("containerHeight = ${my.containerHeight}; containerHeightPhysicalDouble = ${my.containerHeightPhysicalDouble}; containerHeightPhysical = $containerHeightPhysical; windowHeight = $windowHeight; windowHeightPhysical = $windowHeightPhysical; topNavbarHeightPhysical = $topNavbarHeightPhysical; scrollStepPhysical = $scrollStepPhysical")

        byid(const.elementID.dynamicFooter).css("display", "none")
        val origScrollY = containerForScrolling.scrollTop

        val drawPurpleLines = false
        if (drawPurpleLines) {
            jqbody.append("<div id='${const.elementID.cutLineContainer}'></div>")
            var yPhysical = topNavbarHeightPhysical
            while (yPhysical < containerHeightPhysical) {
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
//            if (!isModalShown()) {
                containerForScrolling.scrollTop = requestedY
//            }
            await(delay(100)) // Fucking Chrome...
            clog("Shooting at $requestedY (containerForScrolling.scrollTop = ${containerForScrolling.scrollTop}; requestedYPhysical = $requestedYPhysical)")

            visualShitCaptured.reset()
            shitID = id
            window.postMessage(json("type" to "captureVisualShit"), "*")
            val data = await(visualShitCaptured.promise)
            shots += BrowserShot()-{o->
                o.dataURL = data.dataURL
                o.windowScrollY = containerForScrolling.scrollTop // TODO:vgrechka window -> containerForScrolling
                o.windowScrollYPhysical = containerForScrolling.scrollTop.toPhysicalPixels() // TODO:vgrechka window -> containerForScrolling
            }

            val oldY = containerForScrolling.scrollTop
            containerForScrolling.scrollTop = oldY + 1
            val dy = Math.abs(oldY - containerForScrolling.scrollTop)
            clog("oldY = $oldY; containerForScrolling.scrollTop = ${containerForScrolling.scrollTop}; dy = $dy")

            if (dy < 0.001) break
//            if (isModalShown()) break
        }
        unfreezeShitAfterCapturing()

        val res = await(send(VisualShitCapturedRequest()-{o->
            o.id = shitID
            o.shots = shots
            o.devicePixelRatio = window.devicePixelRatio
            o.headerHeight = const.topNavbarHeight
            o.contentWidth = jq("#topNavbarContainer > nav > .container").outerWidth()
            o.contentLeft = jq("#topNavbarContainer > nav > .container").offset().left.toDouble()
            o.containerHeightPhysical = containerHeightPhysical
            o.modal = isModal
        }))
        clog("Sent captured shit to backend")

        jqbody.css("margin-bottom", "0px")
        byid(const.elementID.cutLineContainer).remove()
        containerForScrolling.scrollTop = origScrollY
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
    jq(".modal-backdrop").removeClass("fade") // Opacity should be turned off right away
    jq(".modal-backdrop").css("background-color", "white")
    jq(".modal-backdrop").css("opacity", 1)
}

fun unfreezeShitAfterCapturing() {
    progressTickerKeyframe100RuleStyle.opacity = "0"
    testArtifactClasses.forEach {jq(".$it").show()}
    jq(".modal-backdrop").addClass("fade")
    jq(".modal-backdrop").css("background-color", "")
    jq(".modal-backdrop").css("opacity", "")
}












