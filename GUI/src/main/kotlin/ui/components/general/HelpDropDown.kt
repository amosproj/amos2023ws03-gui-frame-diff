// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * A dropdown menu with links to the project page and the help page.
 * @param modifier Modifier
 * @return [Unit]
 */
@Composable
fun HelpMenu(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val padding = 8.dp
    Box(modifier = Modifier, contentAlignment = Alignment.TopEnd) {
        Button(
            modifier = modifier.padding(padding),
            onClick = { expanded = !expanded },
        ) {
            Text(
                text = "?",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
            )
        }
        DropdownMenu(
            modifier = modifier.padding(padding),
            offset = DpOffset(padding, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            HyperlinkDropdownMenuItem(
                "Project Page",
                "https://github.com/amosproj/amos2023ws03-gui-frame-diff",
            )
            HyperlinkDropdownMenuItem(
                "Help",
                "https://github.com/amosproj/amos2023ws03-gui-frame-diff/wiki",
            )
        }
    }
}
