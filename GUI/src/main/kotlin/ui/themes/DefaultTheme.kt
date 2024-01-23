package ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val DarkColorPalette =
    darkColorScheme(
        /*
         ---- possible custom color scheme ----
         there are e.g.: primary, secondary, tertiary (also with containers and colors for text on them)

        primary = Color.hsv(265F, 1f, 0.93f),
        onPrimary = Color.White,

        secondary = Color.Gray,
        onSecondary = Color.Black,

        error = Color.Red,
        onError = Color.White,

        background = Color.DarkGray,
        onBackground = Color.White,

        surface = Color.Black,
        onSurface = Color.White,

         */
    )

val MyTypography =
    Typography(
        /*
        ---- possible custom styling for text ----
        different types are: display, headline, title, body, label (in each small, medium, large)

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
