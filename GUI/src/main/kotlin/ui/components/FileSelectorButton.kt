package ui.components

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import javax.swing.JFileChooser

/**
* A Composable function that creates a button with a file selector functionality.
*
* @param buttonText The text to be displayed on the button.
* @param onUpdateResult A function that will be called with the selected file path as a parameter.
*/
@Composable
fun FileSelectorButton(
    buttonText: String,
    onUpdateResult: (String) -> Unit,
) {
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

    Button(onClick = {
        try {
            val path = openFileChooserAndGetPath()
            if (path != null) {
                onUpdateResult(path)
            }
        } catch (e: Exception) {
            println("Error updating result: ${e.localizedMessage}")
        }
    }) {
        Text(buttonText)
    }
}
