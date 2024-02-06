// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

/**
 * WrapTheming is a wrapper for the defaultTheme.
 * @param content the content to be wrapped
 */
@Composable
fun WrapTheming(content: @Composable () -> Unit) {
    defaultTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            content()
        }
    }
}
