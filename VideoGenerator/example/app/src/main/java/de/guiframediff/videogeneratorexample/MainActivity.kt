// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Fabian Seitz <github@seitzfabian.de>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
package de.guiframediff.videogeneratorexample

import VideoGeneratorImpl
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.io.File
import java.util.Random
import java.util.Timer
import kotlin.concurrent.timerTask

class MainActivity : ComponentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()
    private lateinit var randomTextView: TextView
    private lateinit var videoGenerator: VideoGeneratorImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        randomTextView = findViewById(R.id.randomTextView)

        val data = File(Environment.getExternalStorageDirectory(), "Documents/")
        val testOutputDir = File(data, "exampleTestOutput/")
        testOutputDir.mkdir()
        val outputFile = File(testOutputDir, "test.mkv")
        videoGenerator = VideoGeneratorImpl(outputFile.path)

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
    }

    override fun onDestroy() {
        videoGenerator.save()
        super.onDestroy()
    }

    private fun generateRandomNumber() {
        val randomNumber = random.nextInt(100)
        randomTextView.text = "Random Number: $randomNumber"
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
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        videoGenerator.loadFrame(pixels, width, height)
    }
}
