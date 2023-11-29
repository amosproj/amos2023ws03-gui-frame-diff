package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Instrumented test, which will execute on an Android device.
 *
 * Run `./gradlew downloadAndUnzipTestAssets` in the example project root
 * before executing this test to have the files loaded!
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class VideoGeneratorFileSystemTest {
    private lateinit var instrumentationContext: Context
    private lateinit var testInputDir: File
    private lateinit var testOutputDir: File

    @JvmField
    @Rule
    public var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            // android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )

    @Before
    fun setup() {
        // gets the android context in this case instrumented test context
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        val assetSubDir = "screen/"
        val assetsFiles =
            instrumentationContext.resources.assets.list(assetSubDir) ?: throw RuntimeException("")

        // This will copy all files in the androidTest's assets directory onto the device
        val data = File(Environment.getExternalStorageDirectory(), "Documents/")
        testInputDir = File(data, "testInput")
        testOutputDir = File(data, "testOutput")
        if ((!testInputDir.mkdir() && !testInputDir.exists()) ||
            (!testOutputDir.mkdir() && !testOutputDir.exists())
        ) {
            throw RuntimeException("Problem with creating the Input/Output dirs")
        }

        for (assetFileName in assetsFiles) {
            val destFile = File(testInputDir, assetFileName)
            if (!destFile.exists()) {
                val srcStream = instrumentationContext.assets.open(assetSubDir + assetFileName)
                val destStream = FileOutputStream(destFile)
                copyStream(srcStream, destStream)
            } else {
                throw RuntimeException("Problem while creating the File on the Device")
            }
        }
    }

    @Test
    fun useAppContext() {
        val videoGenerator = VideoGeneratorImpl(testOutputDir.path + "/output.mkv")

        assert(testInputDir.exists())

        val files = testInputDir.listFiles()

        assertEquals(169, files.size)

        for (file in files!!) {
            val b: Bitmap = BitmapFactory.decodeFile(file.path)
            val width = b.width
            val height = b.height
            val pixels = IntArray(width * height)
            b.getPixels(pixels, 0, width, 0, 0, width, height)
            videoGenerator.loadFrame(pixels, width, height)
        }
        videoGenerator.save()
    }

    @Throws(IOException::class)
    fun copyStream(
        src: InputStream,
        dest: OutputStream,
    ) {
        val buffer = ByteArray(1024)
        var read: Int
        while (src.read(buffer).also { read = it } != -1) {
            dest.write(buffer, 0, read)
        }
    }

    @After
    fun cleanup() {
        // TODO: extract generated Video from emulator and remove test files
    }
}
