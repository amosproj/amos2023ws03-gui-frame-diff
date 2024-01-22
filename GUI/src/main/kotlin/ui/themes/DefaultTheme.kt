package ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val defaultBackgroundColor = Color.hsv(340f, 0.83f, 0.04f)

val DarkColorPalette =
    darkColorScheme(
        /*primary = Color.hsv(265F, 1f, 0.93f),
        secondary = Color.Gray,
        background = Color.DarkGray,
        surface = Color.Black,
        error = Color.Red,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White,*/
    )

val MyTypography =
    Typography(
        // styling for normal text
        /*bodySmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.White,
            ),*/
        // styling for buttons
        /*button =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            ),*/
    )

@Composable
fun defaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        content = content,
        typography = MyTypography
    )
}
