import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.io.File
import javax.swing.JFileChooser

/**
 * The App composable function is the main entry point for the application.
 * It renders a UI that allows the user to select two videos and display them using VideoPlayer.
 *
 * @return This composable function does not return anything.
 */
@Composable
fun App() {
    var videoFile1 by remember { mutableStateOf<File?>(null) }
    var videoFile2 by remember { mutableStateOf<File?>(null) }

    Row(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).padding(16.dp)) {
            InfoPanel("Video File 1", videoFile1)
            Spacer(Modifier.height(8.dp))
            FileSelectorButton("Video 1") { videoFile1 = it }
        }

        Column(Modifier.weight(1f).padding(16.dp)) {
            InfoPanel("Video File 2", videoFile2)
            Spacer(Modifier.height(8.dp))
            FileSelectorButton("Video 2") { videoFile2 = it }
        }
    }
}

/**
 * The main function starts the application and opens a window.
 *
 * @return Unit
 */
fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

/**
 * Creates a button to select a file.
 *
 * @param name The name or description of the file to be selected.
 * @param onFileSelected The callback function to be invoked when a file is selected.
 */
@Composable
fun FileSelectorButton(name: String, onFileSelected: (File?) -> Unit) {
    Button(onClick = { onFileSelected(selectVideo()) }, modifier = Modifier.fillMaxWidth()) {
        Text("Select $name")
    }
}

/**
 * Composable function to display an info panel with the given name and file.
 *
 * @param name The name to be displayed in the info panel.
 * @param file The file to be displayed in the info panel. Can be null if no file is selected.
 */
@Composable
fun InfoPanel(name: String, file: File?) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 4.dp) {
        Text(text = "$name: ${file?.path ?: "No file selected"}", modifier = Modifier.padding(16.dp))
    }
}

/**
 * Prompts the user to select a video file using a file chooser dialog.
 *
 * @return The selected video file, or null if no file was selected.
 */
fun selectVideo(): File? {
    val chooser = JFileChooser()
    return if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        chooser.selectedFile
    } else {
        null
    }
}
