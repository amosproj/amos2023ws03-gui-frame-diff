package ui.themes

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// sets the default color palette
@Composable
fun wrapTheming(content: @Composable () -> Unit) {
    defaultTheme {
        Surface(color = defaultBackgroundColor) {
            content()
        }
    }
}

val defaultBackgroundColor = Color.hsv(340f, 0.83f, 0.04f)

val DarkColorPalette =
    darkColors(
        primary = Color.hsv(265F, 1f, 0.93f),
        secondary = Color.Gray,
        background = Color.DarkGray,
        surface = Color.Black,
        error = Color.Red,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White,
    )

val MyTypography =
    Typography(
        // styling for normal text
        body1 =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = Color.White,
            ),
        // styling for buttons
        button =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
            ),
    )

@Composable
fun defaultTheme(content: @Composable () -> Unit) {
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        content = content,
        typography = MyTypography,
    )
}
