package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun Tooltip(text: String) {
    val cornerSize = 16.dp
    Popup(
        alignment = Alignment.BottomEnd,
        offset = IntOffset(-24, 0),
    ) {
        // Draw a rectangle shape with rounded corners inside the popup
        Box(
            Modifier
                .background(Color.DarkGray, RoundedCornerShape(cornerSize)),
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                color = Color.White,
            )
        }
    }
}
