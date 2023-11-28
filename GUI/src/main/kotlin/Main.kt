import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.JFileChooser

/**
 * The main method that starts the application.
 */
fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

/**
 * Composable function that represents the main application screen.
 */
@Composable
fun App() {
    var videoPath1 by remember { mutableStateOf<String?>(null) }
    var videoPath2 by remember { mutableStateOf<String?>(null) }
    val mediaPlayerComponent1 = remember { mutableStateOf<EmbeddedMediaPlayerComponent?>(null) }
    val mediaPlayerComponent2 = remember { mutableStateOf<EmbeddedMediaPlayerComponent?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                videoPanel(videoPath1) { path ->
                    videoPath1 = path
                    mediaPlayerComponent1.value?.mediaPlayer()?.media()?.prepare(path)
                }
            }
            Column(Modifier.weight(1f)) {
                videoPanel(videoPath2) { path ->
                    videoPath2 = path
                    mediaPlayerComponent2.value?.mediaPlayer()?.media()?.prepare(path)
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {
                try {
                    mediaPlayerComponent1.value?.mediaPlayer()?.controls()?.play()
                    mediaPlayerComponent2.value?.mediaPlayer()?.controls()?.play()
                } catch (e: Exception) {
                    println("Error playing videos: ${e.localizedMessage}")
                }
            }) {
                Text("Play Videos")
            }
            Spacer(Modifier.width(4.dp))
            Button(onClick = {
                try {
                    mediaPlayerComponent1.value?.mediaPlayer()?.controls()?.stop()
                    mediaPlayerComponent2.value?.mediaPlayer()?.controls()?.stop()
                } catch (e: Exception) {
                    println("Error stopping videos: ${e.localizedMessage}")
                }
            }) {
                Text("Stop Videos")
            }
        }
    }
}

/**
 * Composable function to display a video panel.
 *
 * @param videoPath The path to the video file.
 * @param onFileSelected Callback function to handle file selection.
 */
@Composable
fun videoPanel(
    videoPath: String?,
    onFileSelected: (String) -> Unit,
) {
    val mediaPlayerComponent = remember { mutableStateOf<EmbeddedMediaPlayerComponent?>(null) }

    Column(Modifier.fillMaxWidth().padding(8.dp)) {
        SwingPanel(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            factory = {
                when (videoPath) {
                    null -> JPanel()
                    else -> JPanel().apply {
                        layout = BorderLayout()
                        val mediaPlayer = EmbeddedMediaPlayerComponent()
                        try {
                            add(mediaPlayer, BorderLayout.CENTER)
                            // Initialize the media player with the video
                            mediaPlayer.mediaPlayer()?.media()?.prepare(videoPath)
                            mediaPlayerComponent.value = mediaPlayer
                        } catch (e: Exception) {
                            println("Error preparing media: ${e.localizedMessage}")
                        }
                    }
                }
            },
            update = { panel ->
                // Update code to update the media player component
                try {
                    mediaPlayerComponent.value?.let {
                        panel.removeAll()
                        panel.add(it, BorderLayout.CENTER)
                        panel.revalidate()
                    }
                } catch (e: Exception) {
                    println("Error updating media component: ${e.localizedMessage}")
                }
            },
        )
        Spacer(Modifier.height(8.dp))
        FileSelectorButton("Select Video", onFileSelected)
    }
}

/**
 * A Composable function that creates a button with a file selector functionality.
 *
 * @param buttonText The text to be displayed on the button.
 * @param onUpdateResult A function that will be called with the selected file path as a parameter.
 * @param modifier Optional modifier for the button.
 */
@Composable
fun FileSelectorButton(
    buttonText: String,
    onUpdateResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = {
        try {
            val path = openFileChooserAndGetPath()
            if (path != null) {
                onUpdateResult(path)
            }
        } catch (e: Exception) {
            println("Error updating result: ${e.localizedMessage}")
        }
    }, modifier) {
        Text(buttonText)
    }
}

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
fun openFileChooserAndGetPath(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) fileChooser.selectedFile.absolutePath else null
}
