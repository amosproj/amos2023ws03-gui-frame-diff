package ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.screens.hyperlinkDropdownMenuItem

@Composable
fun helpMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Button(
        modifier = modifier,
        onClick = { expanded = !expanded },
    ) {
        Text("?", fontSize = MaterialTheme.typography.body2.fontSize)
    }

    DropdownMenu(
        modifier = modifier.padding(8.dp),
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        hyperlinkDropdownMenuItem("Project Page", "https://github.com/amosproj/amos2023ws03-gui-frame-diff")
        hyperlinkDropdownMenuItem("Help", "https://github.com/amosproj/amos2023ws03-gui-frame-diff/wiki")
    }
}
