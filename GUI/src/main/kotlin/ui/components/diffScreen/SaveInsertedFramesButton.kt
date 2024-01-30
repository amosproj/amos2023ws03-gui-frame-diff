package ui.components.diffScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.general.openFileSaverAndGetPath
import ui.components.general.showOverwriteConfirmation
import java.io.File
import javax.imageio.ImageIO

/**
 * Button to save all inserted frames as pngs to a zip File Archive.
 *
 * @param navigator The navigator to use to create the archive
 * @param modifier The modifier for the button
 * @return [Unit]
 */
@Composable
fun SaveInsertedFramesButton(
    navigator: FrameNavigation,
    modifier: Modifier = Modifier,
    state: MutableState<AppState>,
) {
    Button(
        modifier = modifier.padding(8.dp),
        onClick = {
            openFileSaverAndGetPath(
                state.value.saveInsertionsPath,
            ) { path -> saveInsertedFramesCallback(navigator, path, state) }
        },
    ) {
        Text(
            text = "Export Inserted",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * Callback for when the user selects a file to save.
 * Saves the path and creates the zip archive.
 * @param navigator The navigator to use to create the archive
 * @param path The path to the file to save to
 * @param state The state of the app
 */
fun saveInsertedFramesCallback(
    navigator: FrameNavigation,
    path: String,
    state: MutableState<AppState>,
) {
    // check path for suffix
    var savePath = path
    if (!savePath.endsWith(".zip")) {
        savePath = "$savePath.zip"
    }
    if (File(savePath).exists()) {
        val overwrite = showOverwriteConfirmation()
        if (!overwrite) {
            return
        }
    }
    state.value.saveInsertionsPath = savePath

    createInsertionsExport(savePath, navigator.getInsertedFrames())
}

/**
 * Creates a zip archive containing all inserted frames as pngs.
 * @param outputPath The path to the zip archive
 * @param frames The frames to save
 */
fun createInsertionsExport(
    outputPath: String,
    frames: List<ImageBitmap>,
) {
    val zipFile = File(outputPath)

    val zip = java.util.zip.ZipOutputStream(zipFile.outputStream())

    for (i in frames.indices) {
        zip.putNextEntry(java.util.zip.ZipEntry("insertion_$i.png"))
        val awtInsertImage = frames[i].toAwtImage()
        ImageIO.write(awtInsertImage, "PNG", zip)
    }
    zip.close()
}
