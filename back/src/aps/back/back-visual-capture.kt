package aps.back

import aps.*
import into.kommon.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.properties.Delegates.notNull

private class CapturedShit(
    val id: String,
    val image: BufferedImage
)

private @Volatile var capturedShit: CapturedShit? = null

fun serveVisualShitCapturedRequest(req: VisualShitCapturedRequest): VisualShitCapturedRequest.Response {
    fun toPhysicalPixels(x: Double) = Math.round(x * req.devicePixelRatio).toInt()

    val imgs = mutableListOf<BufferedImage>()
    val shebangWidth = toPhysicalPixels(req.contentWidth)
    var headerHeightPhysical by notNull<Int>()
    for ((i, shot) in req.shots.withIndex()) {
        val dataURL = shot.dataURL
        val comma = dataURL.indexOfOrDie(",")
        val bytes = Base64.getDecoder().decode(dataURL.substring(comma + 1))
        val img = ByteArrayInputStream(bytes).use {
            ImageIO.read(it)
        }
        imgs += img
        // File("$APS_TEMP/visual-capture/${req.id}--$i.png").writeBytes(bytes)

        if (i == 0) {
            headerHeightPhysical = 0
            while (Color(img.getRGB(0, headerHeightPhysical)) != Color.WHITE) {
                headerHeightPhysical = headerHeightPhysical + 1
                if (headerHeightPhysical > img.height) wtf("Supposed header is so fucking long")
            }
        }
    }

    val shebang = BufferedImage(shebangWidth, req.documentHeightPhysical, BufferedImage.TYPE_INT_ARGB)
    val g = shebang.createGraphics()
    val drawSomeShit = true
    if (drawSomeShit) {
        g.color = Color.PINK
        g.fillRect(0, 0, shebangWidth - 1, req.documentHeightPhysical - 1)
        g.stroke = BasicStroke(3F)
        g.color = Color.GREEN
        g.drawLine(0, 0, shebangWidth - 1, req.documentHeightPhysical - 1)
        g.drawLine(shebangWidth - 1, 0, 0, req.documentHeightPhysical - 1)
    }

    var targetY = 0
    var heightLeft = req.documentHeightPhysical
    for ((i, img) in imgs.withIndex()) {
        val cropTop: Int; val cropHeight: Int
        when (i) {
            0 -> {
                cropTop = 0
                cropHeight = img.height
            }
            imgs.lastIndex -> {
                cropHeight = heightLeft
                cropTop = img.height - cropHeight
            }
            else -> {
                cropTop = headerHeightPhysical
                cropHeight = img.height - headerHeightPhysical
            }
        }

        val cropLeft = toPhysicalPixels(req.contentLeft)

        val croppedImg = img.getSubimage(cropLeft, cropTop, shebangWidth, cropHeight)
        g.drawImage(croppedImg, 0, targetY, null)
        targetY += cropHeight
        heightLeft -= cropHeight
    }

    capturedShit = CapturedShit(req.id, shebang)

    return VisualShitCapturedRequest.Response(
        prevCaptureExists = imageFile(req.id).exists()
    )
}

fun serveSaveCapturedVisualShitRequest(req: SaveCapturedVisualShitRequest): SaveCapturedVisualShitRequest.Response {
    val shit = capturedShit ?: bitch("No shit was captured")
    ImageIO.write(shit.image, "PNG", imageFile(shit.id))
    return SaveCapturedVisualShitRequest.Response()
}

private fun imageFile(id: String) = File("$APS_TEMP/visual-capture/$id.png")

















