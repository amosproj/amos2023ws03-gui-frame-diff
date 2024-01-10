package ui.components.general

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a title text with a tooltip icon next to it.
 * @param modifier [Modifier] containing formatting options from the parent.
 * @param text [String] containing the text to be displayed.
 * @param tooltipText [String] containing the info text for the tooltip.
 * @return [Unit]
 */
@Composable
fun TitleWithInfo(
    modifier: Modifier,
    text: String,
    tooltipText: String,
) {
    Row(modifier = modifier.padding(4.dp)) {
        AutoSizeText(
            text = text,
            modifier = Modifier.padding(2.dp),
        )

        InfoIconWithHover(tooltipText)
    }
}
