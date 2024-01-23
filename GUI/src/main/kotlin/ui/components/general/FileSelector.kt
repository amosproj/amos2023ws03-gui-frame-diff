package ui.components.general

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @param directoryPath The path to the directory to be opened in the file chooser.
 * @param onResult The callback to be called when a file was selected.
 * @return The selected file path, or null if no file was selected.
 */
fun openFileChooserAndGetPath(
    directoryPath: String?,
    onResult: (String) -> Unit,
    allowedFileExtensions: Array<String>? = null,
) {
    val fileChooser = JFileChooser()
    // set the current directory to the given directory path or if null the user's home directory
    fileChooser.currentDirectory = java.io.File(directoryPath ?: System.getProperty("user.home"))
    if (allowedFileExtensions != null) {
        fileChooser.isAcceptAllFileFilterUsed = false
        val filter = FileNameExtensionFilter(allowedFileExtensions.joinToString(", "), *allowedFileExtensions)
        fileChooser.addChoosableFileFilter(filter)
    }
    val result = fileChooser.showOpenDialog(null)
    if (JFileChooser.APPROVE_OPTION == result) {
        onResult(fileChooser.selectedFile.absolutePath)
    }
}

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @param directoryPath The path to the directory to be opened in the file chooser.
 * @param onResult The callback to be called when a file was selected.
 * @return The selected file path, or null if no file was selected.
 */
fun openFileSaverAndGetPath(
    directoryPath: String?,
    onResult: (String) -> Unit,
) {
    val fileChooser = JFileChooser()
    // set the current directory to the given directory path or if null the user's home directory
    fileChooser.currentDirectory = java.io.File(directoryPath ?: System.getProperty("user.home"))
    val result = fileChooser.showSaveDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        onResult(fileChooser.selectedFile.absolutePath)
    }
}
