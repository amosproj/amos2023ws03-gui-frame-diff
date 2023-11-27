import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.*
import java.io.File
import javax.swing.JFileChooser

/**
 * The App composable function is the main entry point for the application.
 * It renders a UI that allows the user to select two videos and display them using VideoPlayer.
 *
 * @return The composable function does not return anything.
 */
@Composable
fun App() {
    var videoFile1 by remember { mutableStateOf<File?>(null) }
    var videoFile2 by remember { mutableStateOf<File?>(null) }

    MaterialTheme {
        Column {
            Button(onClick = { videoFile1 = selectVideo() }) {
                Text("Select video 1")
            }

            Button(onClick = { videoFile2 = selectVideo() }) {
                Text("Select video 2")
            }
        }
    }
}

/**
 * The main function starts the application and opens a window. It sets the onCloseRequest event to
 * exit the application and initializes the App.
 */
fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
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
