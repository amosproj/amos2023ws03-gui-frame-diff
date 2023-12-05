
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SelectVideoScreen {
    /**
     * Composable method to display the select video screen.
     *
     * @param onNavigate Callback function to be called when user clicks on the "Compute differences and navigate" button.
     */
    @Composable
    fun SelectVideoScreen(onNavigate: () -> Unit) {
        // Variables to store paths of the selected videos
        var video1Path by remember { mutableStateOf<String?>("") }
        var video2Path by remember { mutableStateOf<String?>("") }

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
            }
            // Perform your video difference computation here
            Button(
                onClick = onNavigate,
//            enabled = video1Path?.isNotEmpty() == true && video2Path?.isNotEmpty() == true
            ) {
                Text("Compute differences and navigate")
            }
            Text("Selected Video 1 Path: $video1Path")
            Text("Selected Video 2 Path: $video2Path")
        }
    }
}
