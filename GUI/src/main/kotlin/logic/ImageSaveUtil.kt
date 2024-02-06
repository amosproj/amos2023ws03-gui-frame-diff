package logic

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Creates a collage of the 3 videos and saves it to a file.
 * @param outputPath [String] containing the path to save the collage to.
 * @param border [Int] containing the width of the border between the videos.
 * @param titleHeight [Int] containing the height of the title.
 * @param font [java.awt.Font] containing the font to use for the title.
 */
fun createCollage(
    frameGrabber: FrameGrabber,
    diffIndex: Int,
    outputPath: String,
    border: Int = 20,
    titleHeight: Int = 100,
    font: java.awt.Font =
        java.awt.Font(
            "Arial",
            java.awt.Font.BOLD,
            100,
        ),
) {
    val width = frameGrabber.width
    var xOffset = 0
    // create the collage
    val collage =
        BufferedImage(
            width * 3 + border * 2,
            frameGrabber.height + titleHeight,
            BufferedImage.TYPE_INT_RGB,
        )
    val g = collage.createGraphics()
    // fill the background with white
    g.color = Color.WHITE
    g.fillRect(0, 0, collage.width, collage.height)

    // draw the images
    val images =
        listOf(
            frameGrabber.getReferenceVideoFrame(diffIndex),
            frameGrabber.getDiffVideoFrame(diffIndex),
            frameGrabber.getCurrentVideoFrame(diffIndex),
        )
    for (item in images) {
        val img = item.toAwtImage()
        g.drawImage(img, xOffset, titleHeight, null)
        xOffset += width + border
    }

    // draw the titles
    g.color = Color.BLACK
    g.font = font
    xOffset = 0
    for (item in listOf("Reference Video", "Diff", "Current Video")) {
        val metrics = g.fontMetrics
        val x = (width - metrics.stringWidth(item)) / 2
        g.drawString(item, x + xOffset, titleHeight - 10)
        xOffset += width + border
    }

    // save the collage
    val file = File(outputPath)
    ImageIO.write(collage, "png", file)
}

/**
 * Creates a zip archive containing all inserted frames as pngs.
 * @param outputPath The path to the zip archive
 * @param frames The frames to save
 */
fun createInsertionsExport(
    outputPath: String,
    frames: List<ImageBitmap>,
) {
    val zipFile = File(outputPath)

    val zip = java.util.zip.ZipOutputStream(zipFile.outputStream())

    for (i in frames.indices) {
        zip.putNextEntry(java.util.zip.ZipEntry("insertion_$i.png"))
        val awtInsertImage = frames[i].toAwtImage()
        ImageIO.write(awtInsertImage, "PNG", zip)
    }
    zip.close()
}
