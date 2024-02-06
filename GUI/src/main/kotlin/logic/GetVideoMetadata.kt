// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
package logic

import org.bytedeco.javacv.FFmpegFrameGrabber

fun getVideoMetadata(path: String): MutableMap<String, String> {
    val grabber = FFmpegFrameGrabber(path)
    grabber.start()
    val metadata = grabber.metadata
    grabber.stop()
    grabber.release()
    return metadata
}
