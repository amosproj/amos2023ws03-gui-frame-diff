package ui.components.general

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
suspend fun openFileChooserAndGetPath(onResult: (String) -> Unit) {
    val fileChooser = JFileChooser()
    val dialog = fileChooser.showOpenDialog(null)
    if (JFileChooser.APPROVE_OPTION == dialog) {
        // Force the UI to update on the main thread -> makes it smoother for rendering the selected file path
        println("opening ${fileChooser.selectedFile.absolutePath}")
        withContext(Dispatchers.Main) {
            onResult(fileChooser.selectedFile.absolutePath)
        }
    }
}

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
fun openSaveChooserAndGetPath(onResult: (String) -> Unit) {
    val fileChooser = JFileChooser()
    val result = fileChooser.showSaveDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onResult(fileChooser.selectedFile.absolutePath)
    }
}
