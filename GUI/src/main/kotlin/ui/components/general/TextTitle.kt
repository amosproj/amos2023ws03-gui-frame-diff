package ui.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a box to display text.
 * @param text [String] containing the text to be displayed.
 * @return [Unit]
 */

@Composable
fun TextTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(0.dp, 10.dp),
        fontSize = MaterialTheme.typography.displayMedium.fontSize,
    )
}
