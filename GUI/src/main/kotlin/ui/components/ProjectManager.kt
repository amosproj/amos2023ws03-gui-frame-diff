package ui.components

import Screen
import ScreenDeserializer
import ScreenSerializer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import models.AppState
import java.io.File
import javax.swing.JFileChooser

// singleton
object AppConfig {
    val mapper: ObjectMapper =
        ObjectMapper().apply {
            val module =
                SimpleModule().apply {
                    addSerializer(Screen::class.java, ScreenSerializer())
                    addDeserializer(Screen::class.java, ScreenDeserializer())
                }
            registerModule(module)
        }
}

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
                    println("clicked")
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

fun openSaveChooserAndGetPath(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showSaveDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile.absolutePath
    } else {
        null
    }
}

fun handleOpenProject(
    state: MutableState<AppState>,
    path: String,
) {
    val file = File(path).readLines()
    state.value = AppConfig.mapper.readValue<AppState>(file.joinToString(""))
}

fun handleSaveProject(
    state: MutableState<AppState>,
    path: String,
) {
    val jsonData = AppConfig.mapper.writeValueAsString(state.value)
    File("$path.json").writeText(jsonData)
}
