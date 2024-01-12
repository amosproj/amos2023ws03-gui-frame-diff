package ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JOptionPane

/**
 * A Composable function that displays an image wrapped in a row.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param modifier [Modifier] to apply to the element.
 * @return [Unit]
 */
@Composable
fun saveableImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
) {
    var expanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    Row(
        modifier =
            modifier.background(Color.Gray)
                .padding(8.dp)
                .fillMaxWidth(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            bitmap = bitmap.value,
            null,
            modifier =
                Modifier.pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.changes.any { it.isConsumed }) {
                                continue
                            }

                            if (event.buttons.isSecondaryPressed) {
                                expanded.value = true
                            }
                        }
                    }
                },
        )
        DropdownMenu(expanded, bitmap)
    }
}

/**
 * a composable function to expand a dropdown menu
 * @param expanded [Boolean] checks it´s expanded
 * @param bitmap [MutableState] <[ImageBitmap]> contains the bitmap
 */
@Composable
private fun DropdownMenu(
    expanded: MutableState<Boolean>,
    bitmap: MutableState<ImageBitmap>,
) {
    CursorDropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
    ) {
        val scope = rememberCoroutineScope()
        DropdownMenuItem({
            expanded.value = false
            scope.launch(Dispatchers.IO) { openSaveChooserAndGetPath { path -> saveBitmapAsPng(bitmap, path) } }
        }) {
            Text("Save image as png")
        }
    }
}

/**
 * a composable function to save the choosen bitmap as png
 * @param bitmap [MutableState] <[ImageBitmap]> contains the bitmap
 */
private fun saveBitmapAsPng(
    bitmap: MutableState<ImageBitmap>,
    path: String,
) {
    var path = path
    println(path)
    if (!path.endsWith(".png")) {
        path = "$path.png"
    }
    val file = File(path)
    if (file.exists()) {
        val overwrite = showOverwriteConfirmation()
        if (!overwrite) {
            return
        }
    }
    val awtImage = bitmap.value.toAwtImage()
    ImageIO.write(awtImage, "PNG", file)
}

/**
 * Displays a confirmation dialog asking the user if they want to overwrite an existing file.
 *
 * @return true if the user consents to overwrite the file, otherwise false.
 */
fun showOverwriteConfirmation(): Boolean {
    val confirmation =
        JOptionPane.showConfirmDialog(
            null,
            "The file already exists. Do you want to overwrite it?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
        )
    return confirmation == JOptionPane.YES_OPTION
}
