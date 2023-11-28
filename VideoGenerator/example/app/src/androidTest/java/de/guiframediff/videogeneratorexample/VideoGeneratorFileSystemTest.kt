package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import android.content.Context
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

        // This will copy all files in the androidTest's assets directory onto the device
        val data = Environment.getExternalStorageDirectory()
        val testInputDir = File(data, "testInput")
        val testOutputDir = File(data, "testOutput")
        if ((!testInputDir.mkdir() && !testInputDir.exists()) ||
            (!testOutputDir.mkdir() && !testOutputDir.exists())
        ) {
            throw RuntimeException("Problem with creating the Input/Output dirs")
        }
        val assetsFiles =
            instrumentationContext.resources.assets.list("screen") ?: throw RuntimeException("")
        for (assetFile in assetsFiles) {
            val destFile = File(testInputDir, assetFile)
            if (!destFile.exists()) {
                val srcStream = instrumentationContext.assets.open(assetFile)
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
        if (testInputDir.exists()) {
            val files = testInputDir.listFiles()
            for (file in files!!) {
                videoGenerator.loadFrame(file.readBytes())
            }
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
