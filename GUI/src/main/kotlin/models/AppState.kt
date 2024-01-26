package models

import Screen
import ScreenDeserializer
import ScreenSerializer
import algorithms.AlignmentElement
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.nio.file.FileSystems

val defaultOutputPath = getPath("output.mkv")

/**
 * This data class represents the global state of the application.
 * @param screen the current screen
 * @param videoReferencePath the path of the reference video
 * @param videoCurrentPath the path of the current video
 * @param outputPath the path of the output video
 * @param saveCollagePath the last path where a collage was saved
 * @param saveProjectPath the last path where a project was saved
 * @param openProjectPath the last path where a project was opened
 * @param saveInsertionsPath the last path where insertions were saved
 * @param saveFramePath the last path where a frame was saved
 * @param maskPath the path of the mask
 * @param sequenceObj the sequence object
 * @param gapOpenPenalty the gap open penalty
 * @param gapExtendPenalty the gap extend penalty

 */
data class AppState(
    var screen: Screen = Screen.SelectVideoScreen,
    var videoReferencePath: String? = null,
    var videoCurrentPath: String? = null,
    var outputPath: String? = null,
    var saveCollagePath: String? = null,
    var saveProjectPath: String? = null,
    var openProjectPath: String? = null,
    var saveFramePath: String? = null,
    var saveInsertionsPath: String? = null,
    var maskPath: String? = null,
    var sequenceObj: Array<AlignmentElement> = arrayOf(),
    var gapOpenPenalty: Double = 0.2,
    var gapExtendPenalty: Double = -0.8,
)

// singleton Serializer/Deserializer
object JsonMapper {
    val mapper: ObjectMapper =
        ObjectMapper().apply {
            val module =
                SimpleModule().apply {
                    addSerializer(Screen::class.java, ScreenSerializer())
                    addDeserializer(Screen::class.java, ScreenDeserializer())
                }
            registerModule(module)
        }
}

fun createAppState(useDefaultPaths: Boolean): AppState {
    if (useDefaultPaths) {
        return AppState(
            videoReferencePath = getPath("testVideo1.mkv"),
            videoCurrentPath = getPath("testVideo2.mkv"),
            outputPath = defaultOutputPath,
            maskPath = getPath("mask.png"),
        )
    }
    return AppState()
}

/**
 *  TODO: remove this function
 *  This function is used to get the path of a file in the resources folder.
 *  @param name the name of the file
 *  @return [String] the path of the file
 */
private fun getPath(name: String): String {
    return FileSystems.getDefault().getPath("src", "test", "resources", name).toString()
}
