package models

import Screen
import algorithms.AlignmentElement
import java.nio.file.FileSystems

/**
 * This data class represents the global state of the application.
 * @param screen the current screen
 * @param video1Path the path of the first video
 * @param video2Path the path of the second video
 * @param outputPath the path of the output video
 * @param sequenceObj the sequence object
 */
data class AppState(
    var screen: Screen = Screen.SelectVideoScreen,
    var video1Path: String = getPath("testVideo1.mkv"),
    var video2Path: String = getPath("testVideo2.mkv"),
    var outputPath: String = getPath("output.mkv"),
    var sequenceObj: Array<AlignmentElement> = arrayOf(),
    var maskPath: String = getPath("mask.png"),
    var gapOpenPenalty: Double = 0.2,
    var gapExtendPenalty: Double = -0.8,
    var showFilePicker: Boolean = false,
    var filePickerCallback: (String) -> Unit = {},
)

/**
 *  TODO: remove this function
 *  This function is used to get the path of a file in the resources folder.
 *  @param name the name of the file
 *  @return [String] the path of the file
 */
private fun getPath(name: String): String {
    return FileSystems.getDefault().getPath("src", "test", "resources", name).toString()
}
