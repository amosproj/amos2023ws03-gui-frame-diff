package ui.components.general

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a title text with a tooltip icon next to it.
 * @param text [String] containing the text to be displayed.
 * @param tooltipText [String] containing the info text for the tooltip.
 * @param fontSize [TextUnit] containing the fontSize for the title.
 * @param topSpace [Dp] for setting the titles distance to the upper component.
 * @return [Unit]
 */
@Composable
fun TitleWithInfo(
    text: String,
    tooltipText: String,
    fontSize: TextUnit,
    topSpace: Dp,
) {
    Row(
        modifier = Modifier.padding(0.dp, topSpace, 0.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(2.dp),
            fontSize = fontSize,
        )

        InfoIconWithHover(tooltipText)
    }
}
