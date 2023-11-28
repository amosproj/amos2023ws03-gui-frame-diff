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
 * The App composable function is the main entry point for the application.
 * It renders a UI that allows the user to select two videos and display them using VideoPlayer.
 *
 * @return This composable function does not return anything.
 */
@Composable
fun App() {
    var videoPath1 by remember { mutableStateOf<String?>(null) }
    var videoPath2 by remember { mutableStateOf<String?>(null) }

    Row(Modifier.fillMaxSize()) {
        videoPanel(videoPath1, onUpdate = { videoPath1 = it }, Modifier.weight(1f))
        videoPanel(videoPath2, onUpdate = { videoPath2 = it }, Modifier.weight(1f))
    }
}

/**
 * Displays a panel that includes a video player and a file selector button.
 *
 * @param videoPath The path of the video file to be played. If null, an empty panel will be displayed.
 * @param onUpdate Callback function that will be called when a new video path is selected.
 * @param modifier The modifier for the video panel.
 */
@Composable
fun videoPanel(
    videoPath: String?,
    onUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        SwingPanel(
            modifier = Modifier.weight(4f).fillMaxWidth(),
            factory = {
                if (videoPath == null) {
                    JPanel()
                } else {
                    JPanel().apply {
                        layout = BorderLayout()
                        val mediaPlayerComponent = EmbeddedMediaPlayerComponent()
                        add(mediaPlayerComponent, BorderLayout.CENTER)
                        SwingUtilities.invokeLater {
                            mediaPlayerComponent.mediaPlayer().media().play(videoPath)
                        }
                    }
                }
            },
            update = { /* Optional update code here */ },
        )
        Spacer(Modifier.height(8.dp))
        FileSelectorButton("Select Video", onUpdateResult = onUpdate, Modifier.weight(1f).fillMaxWidth())
    }
}

/**
 * The main function starts the application and opens a window.
 *
 * @return Unit indicating that the function does not return a value.
 */
fun main(): Unit = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

/**
 * Creates a button to select a file.
 *
 * @param buttonText The text to be displayed on the button.
 * @param onUpdateResult The callback function to be invoked when a file is selected.
 *                       The selected file is passed as a String parameter to the function.
 * @param modifier Optional modifier for the button.
 */
@Composable
fun FileSelectorButton(
    buttonText: String,
    onUpdateResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(onClick = {
        val path = openFileChooserAndGetPath()
        if (path != null) {
            onUpdateResult(path)
        }
    }, modifier) {
        Text(buttonText)
    }
}

fun openFileChooserAndGetPath(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else {
        null
    }
}
