@file:Suppress("UnsafeCastFromDynamic")

package aps.front

import aps.*
import into.kommon.*
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

fun Double.toPhysicalPixels(): Int = Math.round(this * window.devicePixelRatio)

fun Int.toLayoutPixels(): Double = this / window.devicePixelRatio

fun captureVisualShit(id: String): Promise<Unit> = async {
    val documentHeight: Double = document.documentElement!!.asDynamic().offsetHeight
    val documentHeightPhysical: Int = documentHeight.toPhysicalPixels()
    val windowHeight: Double = window.asDynamic().innerHeight
    val windowHeightPhysical: Int = windowHeight.toPhysicalPixels()
    val topNavbarHeightPhysical: Int = const.topNavbarHeight.toPhysicalPixels()
    val scrollStepPhysical: Int = windowHeightPhysical - topNavbarHeightPhysical
    clog("documentHeight = $documentHeight; documentHeightPhysical = $documentHeightPhysical; windowHeight = $windowHeight; windowHeightPhysical = $windowHeightPhysical; topNavbarHeightPhysical = $topNavbarHeightPhysical; scrollStepPhysical = $scrollStepPhysical")

    byid(const.elementID.dynamicFooter).css("display", "none")
    val origScrollY = window.scrollY

    run { // Purple cut lines
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
    await(delay(60 * 60 * 1000))

    val shots = mutableListOf<BrowserShot>()

    while (true) {
        if (shots.size == 10) bitch("Too many fucking chunks to shoot")

        var requestedY = shots.size * windowHeight
        if (shots.size > 0) requestedY -= const.topNavbarHeight
        requestedY -= 10 // killme
        window.scroll(0.0, requestedY)
        clog("Shooting at $requestedY (${window.scrollY})")

        visualShitCaptured.reset()
        shitID = id
        window.postMessage(json("type" to "captureVisualShit"), "*")
        val data = await(visualShitCaptured.promise)
        shots += BrowserShot()-{o->
            o.dataURL = data.dataURL
            o.windowScrollY = window.scrollY
        }

        val oldY = window.scrollY
        window.scroll(0.0, oldY + 1)
        clog("oldY = $oldY; window.scrollY = ${window.scrollY}; abs = ${Math.abs(oldY - window.scrollY)}")
        if (Math.abs(oldY - window.scrollY) < 0.001) break
    }
    await(send(VisualShitCapturedRequest()-{o->
        o.id = shitID
        o.shots = shots
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












