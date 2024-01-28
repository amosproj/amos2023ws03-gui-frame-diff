package ui.components.diffScreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.general.SvgButton

/**
 * A Composable function that displays a button to open the image in a full screen window.
 * @param buttonModifier [Modifier] to apply to the button.
 * @param onClick function to call when the button is clicked.
 * @return [Unit]
 */
@Composable
fun RowScope.FullScreenButton(
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SvgButton(
        content = "full-screen.svg",
        modifier = buttonModifier.weight(0.3f).sizeIn(maxWidth = 100.dp, maxHeight = 100.dp),
        onClick = onClick,
    )
}
