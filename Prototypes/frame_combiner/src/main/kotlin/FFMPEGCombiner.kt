import java.io.File

class FFMPEGCombiner(private val outputPath: String, private val width: Int, private val height: Int) {
    private var frameCounter = 0

    private var absoluteOutputPath = File(outputPath).absolutePath

    private var resolution: String = width.toString() + "x" + height.toString()

    /**
     * appends frame to output video or generates output video if it's the first frame
     */
    fun addFrame(inputPath: String) {
        val absoluteFilePath = File(inputPath).absolutePath
        if (frameCounter == 0) {
            runCommand("ffmpeg -framerate 30 -i $absoluteFilePath -s $resolution -qp 0 -vf format=yuv420p $absoluteOutputPath")
            frameCounter++
        } else {
            runCommand(
                "ffmpeg -i $absoluteOutputPath -framerate 30 -i $absoluteFilePath -filter_complex [0:v][1:v]concat=n=2:v=1:a=0 -c:v libx264 -qp 0 -preset veryfast -y tmp.$outputPath",
            )
            runCommand("rm $absoluteOutputPath")
            runCommand("mv tmp.$outputPath $absoluteOutputPath")
            frameCounter++
        }
    }

    /**
     * Executes a terminal Commands e.g. a ffmpeg command
     */
    fun runCommand(command: String) {
        try {
            val processBuilder = ProcessBuilder(command.split(" "))
            val process = processBuilder.start()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
