package models

import Screen
import ScreenDeserializer
import ScreenSerializer
import algorithms.AlignmentElement
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import java.nio.file.FileSystems

/**
 * This data class represents the global state of the application.
 * @param screen the current screen
 * @param video1Path the path of the first video
 * @param video2Path the path of the second video
 * @param outputPath the path of the output video
 * @param saveCollagePath the last path where a collage was saved
 * @param saveProjectPath the last path where a project was saved
 * @param openProjectPath the last path where a project was opened
 * @param saveFramePath the last path where a frame was saved
 * @param maskPath the path of the mask
 * @param sequenceObj the sequence object
 * @param gapOpenPenalty the gap open penalty
 * @param gapExtendPenalty the gap extend penalty

 */
data class AppState(
    var screen: Screen = Screen.SelectVideoScreen,
    var video1Path: String = getPath("testVideo1.mkv"),
    var video2Path: String = getPath("testVideo2.mkv"),
    var outputPath: String = getPath("output.mkv"),
    var saveCollagePath: String? = null,
    var saveProjectPath: String? = null,
    var openProjectPath: String? = null,
    var saveFramePath: String? = null,
    var maskPath: String = getPath("mask.png"),
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

/**
 *  TODO: remove this function
 *  This function is used to get the path of a file in the resources folder.
 *  @param name the name of the file
 *  @return [String] the path of the file
 */
private fun getPath(name: String): String {
    return FileSystems.getDefault().getPath("src", "test", "resources", name).toString()
}
