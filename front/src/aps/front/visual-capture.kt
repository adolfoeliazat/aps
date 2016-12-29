package aps.front

import aps.*
import kotlin.browser.window
import kotlin.properties.Delegates.notNull

private val visualShitCaptured = ResolvableShit<Unit>()
private val shitID by notNull<String>()

external interface VisualShitCapturedMessageData {
    val dataURL: String
}

fun visualShitCaptured(data: VisualShitCapturedMessageData) {
    async {
        await(send(VisualShitCapturedRequest()-{o->
            o.id = shitID
            o.shots = listOf(
                BrowserShot()-{o->
                    o.windowScrollY = 0
                    o.dataURL = data.dataURL
                }
            )
        }))
        byid(const.elementID.dynamicFooter).css("display", "")
        clog("Sent captured shit to backend")
        visualShitCaptured.resolve(Unit)
    }
}

fun captureVisualShit(id: String): Promise<Unit> {
    byid(const.elementID.dynamicFooter).css("display", "none")
    visualShitCaptured.reset()
    window.postMessage(json("type" to "captureVisualShit"), "*")
    return visualShitCaptured.promise
}


//fun spikeCaptureVisualShit() {
//    captureVisualShit()
//}












