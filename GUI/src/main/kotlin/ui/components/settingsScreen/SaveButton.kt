// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.settingsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState

/**
 * Save button
 * @param state current state
 * @param oldState old state
 */
@Composable
fun RowScope.SaveButton(
    state: MutableState<AppState>,
    oldState: MutableState<AppState>,
) {
    Button(
        content = {
            Image(
                painter = painterResource("save.svg"),
                contentDescription = "save",
                modifier = Modifier.size(50.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary),
            )
        },
        colors =
            ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary,
            ),
        modifier = Modifier.size(80.dp, 80.dp).clip(CircleShape),
        onClick = {
            oldState.value = state.value
            state.value = oldState.value.copy(screen = Screen.SelectVideoScreen)
        },
        enabled = oldState.value != state.value,
    )
}
