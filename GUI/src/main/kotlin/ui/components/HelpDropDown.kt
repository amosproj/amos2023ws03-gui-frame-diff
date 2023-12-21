package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ui.screens.hyperlinkDropdownMenuItem

@Composable
fun helpMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val padding = 8.dp
    Box(modifier = modifier.fillMaxSize()) {
        Button(
            modifier = modifier.fillMaxSize().padding(padding),
            onClick = { expanded = !expanded },
        ) {
            Text("?", fontSize = MaterialTheme.typography.body2.fontSize)
        }

        DropdownMenu(
            modifier = modifier.padding(padding),
            offset = DpOffset(padding, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            hyperlinkDropdownMenuItem("Project Page", "https://github.com/amosproj/amos2023ws03-gui-frame-diff")
            hyperlinkDropdownMenuItem("Help", "https://github.com/amosproj/amos2023ws03-gui-frame-diff/wiki")
        }
    }
}
