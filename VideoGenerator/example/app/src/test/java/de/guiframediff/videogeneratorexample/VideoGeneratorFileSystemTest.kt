package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import org.junit.Test
import java.io.File

class VideoGeneratorFileSystemTest {
    @Test
    fun generateVideoFromSystemFiles() {
        val videoGenerator = VideoGeneratorImpl("output.mkv")
        val dir = File("src/androidTest/assets/screen")
        if (dir.exists()) {
            val files = dir.listFiles()
            for (file in files!!) {
                videoGenerator.loadFrame(file.readBytes())
            }
        }
        videoGenerator.save()
    }
}
