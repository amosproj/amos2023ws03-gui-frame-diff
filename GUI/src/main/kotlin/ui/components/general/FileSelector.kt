package ui.components.general

import javax.swing.JFileChooser

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @param onResult The callback to be called when a file was selected.
 * @return The selected file path, or null if no file was selected.
 */
fun openFileChooserAndGetPath(onResult: (String) -> Unit) {
    val fileChooser = JFileChooser()
    val result = fileChooser.showOpenDialog(null)
    if (JFileChooser.APPROVE_OPTION == result) {
        onResult(fileChooser.selectedFile.absolutePath)
    }
}

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @param onResult The callback to be called when a file was selected.
 * @return The selected file path, or null if no file was selected.
 */
fun openFileSaverAndGetPath(onResult: (String) -> Unit) {
    val fileChooser = JFileChooser()
    val result = fileChooser.showSaveDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onResult(fileChooser.selectedFile.absolutePath)
    }
}
