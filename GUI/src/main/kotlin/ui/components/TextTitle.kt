package ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a box to display text.
 * @param text [String] containing the text to be displayed.
 * @return [Unit]
 */

@Composable
fun RowScope.textTitle(text: String) {
    AutoSizeText(
        text = text,
        modifier = Modifier.weight(1f).fillMaxSize().padding(20.dp),
    )
}
