package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Slider
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    tooltipText: String? = null,
) {
    // the value of the slider
    var sliderValue = remember { mutableStateOf(default) }
    var textValue = remember { mutableStateOf(default.toString()) }

    // Column contains the slider construct
    Column(modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        // title
        if (tooltipText != null) {
            TitleWithInfo(Modifier.weight(0.5f), title, tooltipText)
        } else {
            AutoSizeText(modifier = Modifier.weight(0.5f).padding(8.dp), text = title)
        }

        // slider
        Row(modifier = Modifier.weight(0.2f)) {
            // min value
            AutoSizeText(text = minValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            // slider
            Slider(
                value = sliderValue.value.toFloat(),
                steps = ((maxValue - minValue) * 100).toInt() - 1,
                onValueChange = {
                    onChange(it.toDouble())
                    sliderValue.value = it.toDouble()
                    textValue.value = String.format("%.2f", it).replace(",", ".")
                },
                valueRange = minValue.toFloat()..maxValue.toFloat(),
                modifier = Modifier.weight(0.5f).padding(8.dp).fillMaxHeight(1f),
            )
            // max value
            AutoSizeText(text = maxValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            // current value
            currentValueInputField(textValue, minValue, maxValue, sliderValue)
        }
    }
}

/**
 * currentValueInputField is a composable that displays the current value of the slider.
 *
 * @param textValue the current value of the slider
 * @param minValue the minimum value of the slider
 * @param maxValue the maximum value of the slider
 * @param sliderValue the value of the slider
 */
@Composable
private fun currentValueInputField(
    textValue: MutableState<String>,
    minValue: Double,
    maxValue: Double,
    sliderValue: MutableState<Double>,
) {
    TextField(
        value = textValue.value,
        onValueChange = { newText ->
            val filteredText = newText.filter { it.isDigit() || it == '.' || it == '-' }
            val newValue = filteredText.toFloatOrNull()
            textValue.value = filteredText
            if (newValue == null) {
                return@TextField
            }
            if (newValue in minValue..maxValue) {
                sliderValue.value = newValue.toDouble()
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
