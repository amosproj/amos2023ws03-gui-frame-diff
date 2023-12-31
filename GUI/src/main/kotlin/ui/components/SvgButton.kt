package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * A Composable function that creates a button.
 * @param onClick [Function] to be called when the button is clicked.
 * @param content [String] containing the name of the svg file to be displayed.
 * @param modifier [Modifier] to be applied to the [Button].
 * @return [Unit]
 */
@Composable
fun svgButton(
    onClick: () -> Unit,
    content: String,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(40.dp, 20.dp, 40.dp, 20.dp),
    ) {
        Image(
            painter = painterResource(content),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
