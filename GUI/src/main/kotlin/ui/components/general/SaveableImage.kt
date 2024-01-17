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
import models.AppState
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JOptionPane

/**
 * A Composable function that displays an image wrapped in a row.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param modifier [Modifier] to apply to the element.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@Composable
fun SaveableImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
    state: MutableState<AppState>,
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
        DropdownMenu(expanded, bitmap, state)
    }
}

/**
 * a composable function to expand a dropdown menu
 * @param expanded [Boolean] checks it´s expanded
 * @param bitmap [MutableState] <[ImageBitmap]> contains the bitmap
 * @param state [MutableState] <[AppState]> contains the state of the app
 */
@Composable
private fun DropdownMenu(
    expanded: MutableState<Boolean>,
    bitmap: MutableState<ImageBitmap>,
    state: MutableState<AppState>,
) {
    CursorDropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
    ) {
        DropdownMenuItem({
            expanded.value = false
            openFileSaverAndGetPath(state.value.saveFramePath) { path -> saveBitmapAsPng(bitmap, path, state) }
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
    state: MutableState<AppState>,
) {
    var savePath = path
    // check path for suffix
    if (!savePath.endsWith(".png")) {
        savePath = "$savePath.png"
    }
    // remember path for next time opening the file chooser
    state.value.saveFramePath = savePath
    val file = File(savePath)
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
