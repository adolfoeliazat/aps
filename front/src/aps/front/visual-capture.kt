package aps.front

import aps.*
import kotlin.browser.window

external interface VisualShitCapturedMessageData {
    val dataURL: String
}

fun visualShitCaptured(data: VisualShitCapturedMessageData) {
    async {
        await(send(VisualShitCapturedRequest()-{o->
            o.id = "pizdature"
            o.shots = listOf(
                BrowserShot()-{o->
                    o.windowScrollY = 0
                    o.dataURL = data.dataURL
                }
            )
        }))
        clog("Sent captured shit to backend")
    }
}

fun spikeCaptureVisualShit() {
    window.postMessage(json("fun" to "captureVisualShit"), "*")
}

