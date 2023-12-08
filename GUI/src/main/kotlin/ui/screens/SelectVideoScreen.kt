package ui.screens
import Screen
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import ui.components.FileSelectorButton

/**
 * A Composable function that creates a screen to select two videos and an output path.
 *
 * @param setScreen A function that will be called if the user selected all three paths and wants to
 * continue.
 * @return [Unit]
 */
@Composable
fun SelectVideoScreen(setScreen: (Screen) -> Unit) {
    // Variables to store paths of the selected videos
    var video1Path by remember { mutableStateOf<String?>("") }
    var video2Path by remember { mutableStateOf<String?>("") }
    var outputPath by remember { mutableStateOf<String?>("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            FileSelectorButton(buttonText = "Select Video 1") { selectedFilePath ->
                // Update video1Path after file being selected
                video1Path = selectedFilePath
            }
            Spacer(modifier = Modifier.width(8.dp))
            FileSelectorButton(buttonText = "Select Video 2") { selectedFilePath ->
                // Update video2Path after file being selected
                video2Path = selectedFilePath
            }
            Spacer(modifier = Modifier.width(8.dp))
            // text input field to set outputPath
            TextField(
                label = { Text("Input Output Path") },
                value = outputPath ?: "",
                onValueChange = { newText ->
                    outputPath = newText
                },
            )
        }
        // Perform your video difference computation here
        Button(
            onClick = {
                DifferenceGeneratorWrapper(
                    video1Path = video1Path!!,
                    video2Path = video2Path!!,
                    outputPath = outputPath!!,
                ).getDifferences()
                setScreen(Screen.DiffScreen)
            },
            enabled =
                video1Path?.isNotEmpty() == true &&
                    video2Path?.isNotEmpty() == true &&
                    outputPath?.isNotEmpty() == true,
        ) {
            Text("Compute differences and navigate")
        }
        Text("Selected Video 1 Path: $video1Path")
        Text("Selected Video 2 Path: $video2Path")
    }
}
