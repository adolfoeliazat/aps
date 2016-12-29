package aps.back

import aps.*
import java.io.File
import java.util.*

fun serveVisualShitCapturedRequest(req: VisualShitCapturedRequest): VisualShitCapturedRequest.Reponse {
    for ((i, shot) in req.shots.withIndex()) {
        val dataURL = shot.dataURL
        val comma = dataURL.indexOfOrDie(",")
        val shit = Base64.getDecoder().decode(dataURL.substring(comma + 1))
        File("$APS_TEMP/visual-capture/${req.id}--$i.png").writeBytes(shit)
    }
    return VisualShitCapturedRequest.Reponse()
}

