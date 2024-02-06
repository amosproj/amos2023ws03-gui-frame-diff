// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
package ui.components.selectVideoScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState

/**
 * A button that opens the advanced settings screen.
 *
 * @param state The state of the app.
 */
@Composable
fun RowScope.AdvancedSettingsButton(state: MutableState<AppState>) {
    Button(
        // fills all available space
        modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            // set the screen
            state.value = state.value.copy(screen = Screen.SettingsScreen)
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        Image(
            painter = painterResource("settings.svg"),
            contentDescription = "settings",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
            colorFilter = ColorFilter.tint(LocalContentColor.current),
        )
    }
}
