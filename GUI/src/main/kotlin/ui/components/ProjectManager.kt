package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import models.AppState
import java.io.File

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
                onClick = { openFileChooserAndGetPath()?.let { handleOpenProject(state, it) } },
            ) {
                Text("Open Project", fontSize = MaterialTheme.typography.body2.fontSize)
            }

            DropdownMenuItem(
                onClick = { openFileChooserAndGetPath()?.let { handleSaveProject(state, it) } },
            ) {
                Text("Save Project", fontSize = MaterialTheme.typography.body2.fontSize)
            }
        }
    }
}

fun handleOpenProject(
    state: MutableState<AppState>,
    path: String,
) {
    val mapper = jacksonObjectMapper()
    val file = File(path).readLines()
    state.value = mapper.readValue<AppState>(file.joinToString(""))
}

fun handleSaveProject(
    state: MutableState<AppState>,
    path: String,
) {
    val mapper = jacksonObjectMapper()
    val jsonData = mapper.writeValueAsString(state.value)
    File(path).writeText(jsonData)
}
