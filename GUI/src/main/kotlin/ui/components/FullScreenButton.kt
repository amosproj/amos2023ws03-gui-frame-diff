package ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A Composable function that displays a button to open the image in a full screen window.
 * @param rowModifier [Modifier] to apply to the row.
 * @param spacerModifier [Modifier] to apply to the spacer.
 * @param buttonModifier [Modifier] to apply to the button.
 * @param onClick [Function0] to call when the button is clicked.
 * @return [Unit]
 */
@Composable
fun ColumnScope.fullScreenButton(
    rowModifier: Modifier = Modifier,
    spacerModifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(rowModifier.weight(0.2f)) {
        Spacer(spacerModifier.weight(0.7f))
        svgButton(content = "full-screen.svg", modifier = buttonModifier.weight(0.3f), onClick = onClick)
    }
}
