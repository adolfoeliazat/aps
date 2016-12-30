@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
import jquery.jq
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private val visualShitCaptured = ResolvableShit<VisualShitCapturedMessageData>()
private var shitID by notNull<String>()

external interface VisualShitCapturedMessageData {
    val dataURL: String
}

fun visualShitCaptured(data: VisualShitCapturedMessageData) {
    visualShitCaptured.resolve(data)
}

fun captureVisualShit(id: String): Promise<Unit> = async {
    val documentHeight: Double = document.documentElement!!.asDynamic().offsetHeight
    val documentHeightPhysical: Int = documentHeight.toPhysicalPixels()
    val windowHeight: Double = window.asDynamic().innerHeight
    val windowHeightPhysical: Int = windowHeight.toPhysicalPixels()
    val topNavbarHeightPhysical: Int = const.topNavbarHeight.toPhysicalPixels()
    val scrollStepPhysical: Int = windowHeightPhysical - topNavbarHeightPhysical
    clog("documentHeight = $documentHeight; documentHeightPhysical = $documentHeightPhysical; windowHeight = $windowHeight; windowHeightPhysical = $windowHeightPhysical; topNavbarHeightPhysical = $topNavbarHeightPhysical; scrollStepPhysical = $scrollStepPhysical")

    byid(const.elementID.dynamicFooter).css("display", "none")
    jq("#footer div").css("border-color", "purple") // killme
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

    while (true) {
        if (shots.size == 10) bitch("Too many fucking chunks to shoot")

        val requestedYPhysical = shots.size * scrollStepPhysical
        val requestedY = requestedYPhysical.toLayoutPixels()
        window.scroll(0.0, requestedY)
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
    }
    await(send(VisualShitCapturedRequest()-{o->
        o.id = shitID
        o.shots = shots
        o.devicePixelRatio = window.devicePixelRatio
        o.headerHeight = const.topNavbarHeight
        o.contentWidth = jq("#topNavbarContainer > nav > .container").outerWidth()
        o.contentLeft = jq("#topNavbarContainer > nav > .container").offset().left.toDouble()
    }))
    clog("Sent captured shit to backend")

    byid(const.elementID.cutLineContainer).remove()
    window.scroll(0.0, origScrollY)
    byid(const.elementID.dynamicFooter).css("display", "")
    Unit
}



//fun spikeCaptureVisualShit() {
//    captureVisualShit()
//}












