import org.junit.Assert.*
import java.io.File

class FFMPEGCombinerTest {

    @org.junit.Test(timeout = Long.MAX_VALUE)
    fun addFrame() {
        val outputFileName = "output.mkv"
        val height = 1024
        val width = 1024
        val ffmpegCombiner = FFMPEGCombiner(outputFileName, width, height)

        //clean old video output
        ffmpegCombiner.runCommand("rm $outputFileName")

        println("starting mkv generation\n")
        for (i in 0..255) {
            val index = "%03d".format(i) // 1 -> 001
            ffmpegCombiner.addFrame("src/main/resources/triangle/tmp.$index.png")
            println("appended Frame: $index")
        }
        println("finished mkv generation")
        assert(File(outputFileName).exists())
    }
}