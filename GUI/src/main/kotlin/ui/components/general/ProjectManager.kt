package ui.components.general

import Screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.AppState
import models.JsonMapper
import java.io.File

/**
 * Dropdown menu to open and save projects
 * @param state the current state of the app
 * @param modifier the modifier to apply to the component
 *
 * @return a dropdown menu to open and save projects
 */
@Composable
fun ProjectMenu(
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
            Text("Project", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
        }

        DropdownMenu(
            modifier = modifier.padding(padding),
            offset = DpOffset(padding, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            val openScope = rememberCoroutineScope()
            DropdownMenuItem(
                {
                    Text("Open Project", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                },
                onClick = {
                    openScope.launch(
                        Dispatchers.IO,
                    ) { openFileChooserAndGetPath(state.value.openProjectPath) { path -> handleOpenProject(state, path) } }
                    expanded = false
                }
            )

            val saveScope = rememberCoroutineScope()
            DropdownMenuItem(
                {
                    Text("Save Project", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                },
                onClick = {
                    saveScope.launch(
                        Dispatchers.IO,
                    ) { openFileSaverAndGetPath(state.value.saveProjectPath) { path -> handleSaveProject(state, path) } }
                    expanded = false
                },
                enabled = state.value.screen == Screen.DiffScreen,
            )
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
    state.value = JsonMapper.mapper.readValue<AppState>(file.joinToString(""))
    state.value.openProjectPath = path
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
    var savePath = path
    if (!savePath.endsWith(".json")) {
        savePath = "$savePath.json"
    }
    state.value.saveProjectPath = savePath

    val jsonData = JsonMapper.mapper.writeValueAsString(state.value)
    File(savePath).writeText(jsonData)
}
