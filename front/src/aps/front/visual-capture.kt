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

fun captureVisualShit(id: String): Promise<Unit> = async {
    val documentHeight: Double = document.documentElement!!.asDynamic().offsetHeight
    val windowHeight: Double = window.asDynamic().innerHeight
    clog("documentHeight = $documentHeight; windowHeight = $windowHeight")

    byid(const.elementID.dynamicFooter).css("display", "none")
    val origScrollY = window.scrollY

    run { // Purple cut lines
        jqbody.append("<div id='${const.elementID.cutLineContainer}'></div>")
        var y = 0.0
        while (y < documentHeight) {
            byid(const.elementID.cutLineContainer).append(
                // Moving from bottom in order to prevent accidental document height growth
                "<div class='${css.test.cutLine}' style='bottom: ${y}px;'></div>")
            y += windowHeight
        }
    }

    val shots = mutableListOf<BrowserShot>()

    while (true) {
        // await(delay(5000))
        if (shots.size == 10) bitch("Too many fucking chunks to shoot")

        var requestedY = shots.size * windowHeight
        if (shots.size > 0) requestedY -= const.topNavbarHeight
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












