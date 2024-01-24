package ui.components.settingsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState

/**
 * Back button component
 *
 * @param state current app state
 * @param oldState previous app state
 */
@Composable
fun RowScope.BackButton(
    state: MutableState<AppState>,
    oldState: MutableState<AppState>,
) {
    Button(
        // fills all available space
        modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxSize(1f),
        onClick = { state.value = oldState.value.copy(screen = Screen.SelectVideoScreen) },
    ) {
        Image(
            painter = painterResource("back-arrow.svg"),
            contentDescription = "back",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
        )
    }
}
