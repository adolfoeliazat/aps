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

fun serveVisualShitCapturedRequest(req: VisualShitCapturedRequest): VisualShitCapturedRequest.Reponse {
    fun toPhysicalPixels(x: Double) = Math.round(x * req.devicePixelRatio).toInt()

    val imgs = mutableListOf<BufferedImage>()
    var shebangHeight = 0
    val shebangWidth = toPhysicalPixels(req.contentWidth) // TODO:vgrechka Determine visually
    var headerHeightPhysical by notNull<Int>()
    var footerHeightPhysical by notNull<Int>()
    for ((i, shot) in req.shots.withIndex()) {
        val dataURL = shot.dataURL
        val comma = dataURL.indexOfOrDie(",")
        val bytes = Base64.getDecoder().decode(dataURL.substring(comma + 1))
        val img = ByteArrayInputStream(bytes).use {
            ImageIO.read(it)
        }
        imgs += img
        File("$APS_TEMP/visual-capture/${req.id}--$i.png").writeBytes(bytes)

        if (i == 0) {
            shebangHeight += img.height

            headerHeightPhysical = 0
            while (Color(img.getRGB(0, headerHeightPhysical)) != Color.WHITE) {
                headerHeightPhysical = headerHeightPhysical + 1
                if (headerHeightPhysical > img.height) wtf("Supposed header is so fucking long")
            }
        } else {
            shebangHeight += img.height - headerHeightPhysical
        }
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
        val cropTop: Int; val cropHeight: Int
        if (i == 0) {
            cropTop = 0
            cropHeight = img.height
        } else {
            cropTop = headerHeightPhysical
            cropHeight = img.height - headerHeightPhysical
        }

        val cropLeft = toPhysicalPixels(req.contentLeft)

        val croppedImg = img.getSubimage(cropLeft, cropTop, shebangWidth, cropHeight)
        g.drawImage(croppedImg, 0, targetY, null)
        targetY += cropHeight
    }

    ImageIO.write(shebang, "PNG", File("$APS_TEMP/visual-capture/shebang.png"))
    return VisualShitCapturedRequest.Reponse()
}



