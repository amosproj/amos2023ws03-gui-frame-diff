package ui.components.general

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
 * @param fixedFontSize [Int] to apply to the text. Defaults to null.
 * @return [Unit]
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = LocalContentColor.current,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
    minimalFontSize: Int = 1,
    fixedFontSize: Int? = null,
) {
    // create a state variable to hold the current text
    var currentText by remember { mutableStateOf(text) }
    // create a state variable to hold the original text
    var originalText by remember { mutableStateOf(text) }
    // update the visible text if the original text changes
    if (originalText != text) {
        currentText = text
        originalText = text
    }
    // create a state variable to hold the scaled style
    var scaled by remember { mutableStateOf(style) }

    // create a state variable to hold the text measurement
    val textMeasurer = rememberTextMeasurer()

    // fancy density magic
    val localDensity = LocalDensity.current
    // reapply default parameters
    Text(
        text = currentText,
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
            val width =
                if (result.layoutInput.text.isEmpty()) {
                    0
                } else {
                    result.layoutInput.constraints.maxWidth / result.layoutInput.text.length
                }
            // choose limit based on height or width or minimalFontSize
            if (fixedFontSize != null) {
                scaled = scaled.copy(fontSize = with(localDensity) { fixedFontSize.toSp() })
            } else {
                scaled = scaled.copy(fontSize = with(localDensity) { maxOf(minOf(height, width), minimalFontSize).toSp() })
            }
            // on overflow trim start
            // measure the text with the scaled style
            var data = textMeasurer.measure(currentText, scaled, overflow = TextOverflow.Visible)
            // trim the text until it fits
            while (data.size.width > result.layoutInput.constraints.maxWidth) {
                currentText = currentText.substring(1, currentText.length)
                data = textMeasurer.measure(currentText, scaled, overflow = TextOverflow.Visible)
            }
            // add ellipsis if the text was trimmed
            if (currentText.length < text.length && currentText.length > 3) {
                currentText = "..." + currentText.substring(3, currentText.length)
            }
        },
        // draw the text with the scaled style
        modifier = modifier.drawWithContent { drawContent() },
        // a re-compose is forced once on scaling since drawContent does not account for changed text
    )
}
