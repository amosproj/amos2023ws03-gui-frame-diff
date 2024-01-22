package ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val defaultBackgroundColor = Color.hsv(340f, 0.83f, 0.04f)

val DarkColorPalette =
    darkColorScheme(
        /* possible custom color scheme
        primary = Color.hsv(265F, 1f, 0.93f),
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
        /* possible styling for normal text
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.White,
            )*/
    )

@Composable
fun defaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        content = content,
        typography = MyTypography,
    )
}
