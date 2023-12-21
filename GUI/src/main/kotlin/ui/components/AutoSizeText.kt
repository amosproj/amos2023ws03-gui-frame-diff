package ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * A Composable function that creates a text element that automatically scales to fit the
 * available space.
 * @param text [String] to display.
 * @param modifier [Modifier] to apply to the element.
 * @param color [Color] to apply to the text.
 * @param fontStyle [FontStyle] to apply to the text.
 * @param fontWeight [FontWeight] to apply to the text.
 * @param fontFamily [FontFamily] to apply to the text.
 * @param letterSpacing [TextUnit] to apply to the text.
 * @param textDecoration [TextDecoration] to apply to the text.
 * @param textAlign [TextAlign] to apply to the text.
 * @param lineHeight [TextUnit] to apply to the text.
 * @param overflow [TextOverflow] to apply to the text.
 * @param maxLines [Int] to apply to the text.
 * @param style [TextStyle] to apply to the text.
 * @return [Unit]
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    minimalFontSize: Int = 1,
) {
    // create a state variable to hold the scaled style
    var scaled by remember { mutableStateOf(style) }
    // fancy density magic
    val localDensity = LocalDensity.current
    // reapply default parameters
    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = false,
        style = scaled,
        onTextLayout = { result ->
            val height = result.layoutInput.constraints.maxHeight / 2
            val width = result.layoutInput.constraints.maxWidth / result.layoutInput.text.length
            scaled =
                scaled.copy(
                    // choose limit based on height or width or minimalFontSize
                    fontSize = with(localDensity) { maxOf(minOf(height, width), minimalFontSize).toSp() },
                )
        },
        // draw the text with the scaled style
        modifier = modifier.drawWithContent { drawContent() },
    )
}
