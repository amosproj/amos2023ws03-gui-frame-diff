package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.nio.ByteBuffer
import java.util.Random
import java.util.Timer
import kotlin.concurrent.timerTask

class MainActivity : ComponentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()
    private lateinit var randomTextView: TextView
    private val videoGenerator = VideoGeneratorImpl("test.gif", 1920, 720)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        randomTextView = findViewById(R.id.randomTextView)
        Timer().schedule(
            timerTask {
                handler.post {
                    generateRandomNumber()
                    takeScreenshot()
                }
            },
            0,
            5000,
        )
        videoGenerator.processFrames()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoGenerator.save()
    }

    private fun generateRandomNumber() {
        val randomNumber = random.nextInt(100)
        randomTextView.text = buildString {
        append("Random Number: ")
        append(randomNumber)
    }
    }

    private fun takeScreenshot() {
        val view: View = window.decorView.rootView
        val bitmap = Bitmap.createBitmap(1920, 720, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        val size = bitmap.rowBytes * bitmap.height
        val byteBuffer = ByteBuffer.allocate(size)
        bitmap.copyPixelsToBuffer(byteBuffer)
        videoGenerator.loadFrame(byteBuffer.array())
    }
}
