package ui.components.general

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a button.
 * @param onClick [Function] to be called when the button is clicked.
 * @param content [String] containing the name of the svg file to be displayed.
 * @param enabled [Boolean] deciding if the button can be pressed.
 * @param modifier [Modifier] to be applied to the [Button].
 * @return [Unit]
 */
@Composable
fun SvgButton(
    onClick: () -> Unit,
    content: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(40.dp, 20.dp),
        enabled = enabled,
    ) {
        Image(
            painter = painterResource(content),
            contentDescription = null,
            modifier = Modifier,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
        )
    }
}
