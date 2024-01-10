package ui.components.general

import javax.swing.JFileChooser

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

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
fun openSaveChooserAndGetPath(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showSaveDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else {
        null
    }
}
