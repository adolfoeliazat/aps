package aps.back

import aps.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun serveVisualShitCapturedRequest(req: VisualShitCapturedRequest): VisualShitCapturedRequest.Reponse {
    fun toPhysicalPixels(x: Double) = Math.round(x * req.devicePixelRatio).toInt()

    val imgs = mutableListOf<BufferedImage>()
    for ((i, shot) in req.shots.withIndex()) {
        val dataURL = shot.dataURL
        val comma = dataURL.indexOfOrDie(",")
        val bytes = Base64.getDecoder().decode(dataURL.substring(comma + 1))
        val img = ByteArrayInputStream(bytes).use {
            ImageIO.read(it)
        }
        imgs += img

        File("$APS_TEMP/visual-capture/${req.id}--$i.png").writeBytes(bytes)
    }

    val shebangWidth = toPhysicalPixels(req.contentWidth)
    var shebangHeight = 0
    for ((i, img) in imgs.withIndex()) {
        var usefulHeight = img.height
        if (i > 0) usefulHeight -= toPhysicalPixels(req.headerHeight)
        shebangHeight += usefulHeight
    }

    val shebang = BufferedImage(shebangWidth, shebangHeight, imgs.first().type)
    val g = shebang.createGraphics()
    g.color = Color.PINK
    g.fillRect(0, 0, shebangWidth - 1, shebangHeight - 1)
    g.stroke = BasicStroke(3F)
    g.color = Color.GREEN
    g.drawLine(0, 0, shebangWidth - 1, shebangHeight - 1)
    g.drawLine(shebangWidth - 1, 0, 0, shebangHeight - 1)

    var targetY = 0
    for ((i, img) in imgs.withIndex()) {
        val cropLeft = toPhysicalPixels(req.contentLeft)
        val cropTop =
            if (i == 0) 0
            else toPhysicalPixels(req.headerHeight)
        val cropHeight =
            if (i == 0) img.height
            else img.height - toPhysicalPixels(req.headerHeight)
        val croppedImg = img.getSubimage(cropLeft, cropTop, shebangWidth, cropHeight)
        g.drawImage(croppedImg, 0, targetY, null)
        targetY += cropHeight
    }

    ImageIO.write(shebang, "PNG", File("$APS_TEMP/visual-capture/shebang.png"))
    return VisualShitCapturedRequest.Reponse()
}



