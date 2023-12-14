package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Title is a composable that displays a title.
 *
 * @param title the title to display
 * @param default the default value of the slider
 * @param minValue the minimum value of the slider
 * @param maxValue the maximum value of the slider
 * @param onChange the function to call when the slider value changes
 */
@Composable
fun RowScope.CustomSlider(
    title: String,
    default: Double,
    minValue: Double,
    maxValue: Double,
    onChange: (Double) -> Unit,
) {
    // the value of the slider
    var sliderValue by remember { mutableStateOf(default) }
    // Column contains the slider construct
    Column(modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f)) {
        // title
        AutoSizeText(text = title, modifier = Modifier.weight(0.5f).padding(8.dp).fillMaxHeight(1f).align(Alignment.CenterHorizontally))
        // slider
        Row(modifier = Modifier.weight(0.2f)) {
            // min value
            AutoSizeText(text = minValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            // slider
            Slider(
                value = sliderValue.toFloat(),
                steps = ((maxValue - minValue) * 100).toInt() - 1,
                onValueChange = {
                    onChange(it.toDouble())
                    sliderValue = it.toDouble()
                },
                valueRange = minValue.toFloat()..maxValue.toFloat(),
                modifier = Modifier.weight(0.5f).padding(8.dp).fillMaxHeight(1f),
            )
            // max value
            AutoSizeText(text = maxValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            // current value
            AutoSizeText(text = String.format("%.2f", sliderValue), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
        }
    }
}
