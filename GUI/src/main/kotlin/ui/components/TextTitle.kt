package ui.components

import androidx.compose.foundation.layout.*
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

/**
 * A Composable function that creates a title text with a tooltip icon next to it.
 * @param modifier [Modifier] containing formatting options from the parent.
 * @param text [String] containing the text to be displayed.
 * @param tooltipText [String] containing the info text for the tooltip.
 * @return [Unit]
 */
@Composable
fun TextTitleWithInfo(
    modifier: Modifier,
    text: String,
    tooltipText: String,
) {
    Row(modifier = modifier.padding(8.dp)) {
        AutoSizeText(
            text = text,
            modifier = Modifier.padding(0.dp),
        )

        InfoIconWithHover(tooltipText)
    }
}
