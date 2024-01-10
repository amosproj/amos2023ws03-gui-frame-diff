package ui.components

import Screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.module.kotlin.readValue
import models.AppState
import models.JSONmapper
import java.io.File

/**
 * Dropdown menu to open and save projects
 * @param state the current state of the app
 * @param modifier the modifier to apply to the component
 *
 * @return a dropdown menu to open and save projects
 */
@Composable
fun projectMenu(
    state: MutableState<AppState>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val padding = 8.dp
    Box(modifier = modifier.fillMaxSize()) {
        Button(
            modifier = modifier.fillMaxSize().padding(padding),
            onClick = { expanded = !expanded },
        ) {
            Text("Project", fontSize = MaterialTheme.typography.body2.fontSize)
        }

        DropdownMenu(
            modifier = modifier.padding(padding),
            offset = DpOffset(padding, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                onClick = {
                    openFileChooserAndGetPath()?.let { handleOpenProject(state, it) }
                    expanded = false
                },
            ) {
                Text("Open Project", fontSize = MaterialTheme.typography.body2.fontSize)
            }

            DropdownMenuItem(
                onClick = {
                    openSaveChooserAndGetPath()?.let { handleSaveProject(state, it) }
                    expanded = false
                },
                enabled = state.value.screen == Screen.DiffScreen,
            ) {
                Text("Save Project", fontSize = MaterialTheme.typography.body2.fontSize)
            }
        }
    }
}

/**
 * Reads the given json file and sets the state to the parsed AppState
 * @param state the current state of the app
 * @param path the path to the file to open
 */
fun handleOpenProject(
    state: MutableState<AppState>,
    path: String,
) {
    val file = File(path).readLines()
    state.value = JSONmapper.mapper.readValue<AppState>(file.joinToString(""))
}

/**
 * Opens the json and writes the current state to the file
 * @param state the current state of the app
 * @param path the path to the file to save
 */
fun handleSaveProject(
    state: MutableState<AppState>,
    path: String,
) {
    val jsonData = JSONmapper.mapper.writeValueAsString(state.value)
    File("$path.json").writeText(jsonData)
}
