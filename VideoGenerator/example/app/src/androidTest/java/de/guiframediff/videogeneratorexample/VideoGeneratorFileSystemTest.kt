package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class VideoGeneratorFileSystemTest {

    @Test
    fun useAppContext() {
        val videoGenerator = VideoGeneratorImpl("/storage/1B15-3010/Videos/output.mkv", 1920, 720)
        val dir = File("/storage/1B15-3010/Pictures/screen")
        if (dir.exists()) {
            val files = dir.listFiles()
            for (file in files!!) {
                videoGenerator.loadFrame(file.readBytes())
            }
        }
        videoGenerator.save()
    }
}
