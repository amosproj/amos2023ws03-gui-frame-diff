// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Fabian Seitz <github@seitzfabian.de>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.diffScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import logic.FrameGrabber
import logic.createInsertionsExport
import models.AppState
import ui.components.general.openFileSaverAndGetPath
import ui.components.general.showOverwriteConfirmation
import java.io.File

/**
 * Button to save all inserted frames as pngs to a zip File Archive.
 *
 * @param frameGrabber The frame grabber to use to get the inserted frames
 * @param modifier The modifier for the button
 * @return [Unit]
 */
@Composable
fun SaveInsertedFramesButton(
    frameGrabber: FrameGrabber,
    modifier: Modifier = Modifier,
    state: MutableState<AppState>,
) {
    Button(
        modifier = modifier.padding(8.dp),
        onClick = {
            openFileSaverAndGetPath(
                state.value.saveInsertionsPath,
            ) { path -> saveInsertedFramesCallback(frameGrabber = frameGrabber, path, state) }
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
 * @param frameGrabber The frame grabber to use to get the inserted frames
 * @param path The path to the file to save to
 * @param state The state of the app
 */
fun saveInsertedFramesCallback(
    frameGrabber: FrameGrabber,
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

    createInsertionsExport(savePath, frameGrabber.getInsertedFrames())
}
