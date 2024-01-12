package ui.components.general

import javax.swing.JFileChooser

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
fun openFileChooserAndGetPath(onResult: (String) -> Unit) {
    val fileChooser = JFileChooser()
    val dialog = fileChooser.showOpenDialog(null)
    if (JFileChooser.APPROVE_OPTION == dialog) {
        onResult(fileChooser.selectedFile.absolutePath)
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
