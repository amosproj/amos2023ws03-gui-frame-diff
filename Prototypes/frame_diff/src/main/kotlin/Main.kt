import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun getDiff(i1: String, i2: String, o: String) {
    val image1 = ImageIO.read(File(i1))
    val image2 = ImageIO.read(File(i2))
    val diffImage = BufferedImage(image1.width, image1.height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until image1.width) {
        for (y in 0 until image1.height) {
            val p1 = Color(image1.getRGB(x, y))
            val p2 = Color(image2.getRGB(x, y))


            if (p1.rgb != p2.rgb) {
                diffImage.setRGB(x, y, Color.RED.rgb)
            }
        }
    }


    ImageIO.write(diffImage, "png", File(o))

}

fun main() {
    val image1Path = "Screenshot_51.png"
    val image2Path = "Screenshot_52.png"
    val outputImagePath = "difference.png"
    getDiff(image1Path, image2Path, outputImagePath)
}
