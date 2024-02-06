// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.settingsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ui.components.general.TitleWithInfo

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
    Column(modifier = Modifier.weight(1f).padding(10.dp).fillMaxHeight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        // title
        if (tooltipText != null) {
            TitleWithInfo(title, tooltipText, MaterialTheme.typography.headlineSmall.fontSize, 2.dp)
        } else {
            Text(modifier = Modifier.weight(0.5f).padding(8.dp), text = title)
        }

        // slider
        Row(modifier = Modifier.weight(0.2f), verticalAlignment = Alignment.CenterVertically) {
            // min value
            Text(text = minValue.toString(), modifier = Modifier.padding(2.dp))
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
                modifier = Modifier.fillMaxHeight(1f).fillMaxWidth(0.5f),
            )
            // max value
            Text(text = maxValue.toString(), modifier = Modifier.padding(2.dp))
            // current value
            CurrentValueInputField(textValue, minValue, maxValue, sliderValue)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentValueInputField(
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
        modifier = Modifier.padding(8.dp),
    )
}
