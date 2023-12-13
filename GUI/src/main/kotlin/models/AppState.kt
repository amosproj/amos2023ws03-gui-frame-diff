package models

import Screen
import algorithms.AlignmentElement
import java.nio.file.FileSystems

data class AppState(
    var screen: Screen = Screen.SelectVideoScreen,
    var video1Path: String = getPath("9Screenshots.mov"),
    var video2Path: String = getPath("10ScreenshotsModified.mov"),
    var outputPath: String = getPath("output.mov"),
    var sequenceObj: Array<AlignmentElement> = arrayOf<AlignmentElement>(),
)

/**
 *  TODO: remove this function
 *  This function is used to get the path of a file in the resources folder.
 *  @param name the name of the file
 *  @return [String] the path of the file
 */
private fun getPath(name: String): String {
    return FileSystems.getDefault().getPath("src", "main", "resources", name).toString()
}
