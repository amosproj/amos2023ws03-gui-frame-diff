package ui.components.diffScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * A Composable function that displays a button to open the image in a full screen window.
 * @param buttonModifier [Modifier] to apply to the button.
 * @param onClick function to call when the button is clicked.
 * @return [Unit]
 */
@Composable
fun FullScreenButton(
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = buttonModifier,
        content = {
            Image(
                painter = painterResource("full-screen.svg"),
                contentDescription = "Fullscreen Button",
                modifier = Modifier.padding(8.dp),
                colorFilter = ColorFilter.tint(LocalContentColor.current),
            )
        },
    )
}
