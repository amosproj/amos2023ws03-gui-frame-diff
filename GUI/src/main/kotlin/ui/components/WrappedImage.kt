package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

/**
 * A Composable function that displays an image wrapped in a row.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param modifier [Modifier] to apply to the element.
 * @return [Unit]
 */
@Composable
fun wrappedImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.background(Color.Gray)
                .padding(8.dp)
                .fillMaxWidth(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) { Image(bitmap = bitmap.value, null) }
}
