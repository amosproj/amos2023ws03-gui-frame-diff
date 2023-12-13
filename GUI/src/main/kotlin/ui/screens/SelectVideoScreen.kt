package ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import models.AllVideos
import models.AppState
import ui.components.AutoSizeText
import ui.components.FileSelectorButton
import java.nio.file.FileSystems

/**
 *  TODO: remove this function
 *  This function is used to get the path of a file in the resources folder.
 *  @param name the name of the file
 *  @return [String] the path of the file
 */
private fun getPath(name: String): String {
    return FileSystems.getDefault().getPath("src", "main", "resources", name).toString()
}

@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    // TODO: initialize with empty string
    var video1Path by remember { mutableStateOf(getPath("9Screenshots.mov")) }
    var video2Path by remember { mutableStateOf(getPath("10ScreenshotsModified.mov")) }
    var outputPath by remember { mutableStateOf(getPath("output.mov")) }

    // column represents the whole screen
    Column(modifier = Modifier.fillMaxSize()) {
        // video selection
        Row(modifier = Modifier.weight(0.85f)) {
            FileSelectorButton(
                buttonText = "Select Video 1",
                buttonPath = video1Path,
                onUpdateResult = { selectedFilePath ->
                    video1Path = selectedFilePath
                },
            )

            FileSelectorButton(
                buttonText = "Select Video 2",
                buttonPath = video2Path,
                onUpdateResult = { selectedFilePath ->
                    video2Path = selectedFilePath
                },
            )
        }
        // button to compute the differences
        Row(modifier = Modifier.weight(0.15f)) {
            ComputeDifferencesButton(video1Path, video2Path, outputPath, state)
        }
    }
}

/**
 * A Composable function that creates a button to compute the differences between two videos.
 *
 * @param video1Path the path of the first video
 * @param video2Path the path of the second video
 * @param outputPath the path of the output video
 * @param setScreen a function to set the screen
 * @return [Unit]
 */
@Composable
fun RowScope.ComputeDifferencesButton(
    video1Path: String,
    video2Path: String,
    outputPath: String,
    state: MutableState<AppState>,
) {
    Button(
        // fills all availible space
        modifier = Modifier.weight(1f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            // generate the differences
            val generator =
                DifferenceGeneratorWrapper(
                    video1Path = video1Path,
                    video2Path = video2Path,
                    outputPath = outputPath,
                )
            generator.getDifferences()
            // set the screen
//            setScreen(AllVideos(video1Path, video2Path, outputPath), generator.getSequence())
            state.value =
                state.value.copy(
                    screen = Screen.DiffScreen,
                    sequenceObj = generator.getSequence(),
                    pathObj = AllVideos(video1Path, video2Path, outputPath),
                )
        },
        // enable the button only if all the paths are not empty
        enabled =
            video1Path.isNotEmpty() && video2Path.isNotEmpty() && outputPath.isNotEmpty(),
    ) {
        AutoSizeText(
            text = "Compute and Display Differences",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            // remove default centering
            modifier = Modifier,
        )
    }
}
