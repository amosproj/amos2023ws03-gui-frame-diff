package ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import frameNavigation.FrameNavigation

@Composable
fun NavigationButtons(
    navigator: FrameNavigation,
    buttonModifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
) {
    Row(modifier = rowModifier.fillMaxWidth()) {
        svgButton(onClick = { navigator.jumpToNextDiff(false) }, content = "skipStart.svg", modifier = buttonModifier)
        svgButton(onClick = { navigator.jumpFrames(-1) }, content = "skipPrev.svg", modifier = buttonModifier)
        svgButton(onClick = { navigator.jumpFrames(1) }, content = "skipNext.svg", modifier = buttonModifier)
        svgButton(onClick = { navigator.jumpToNextDiff(true) }, content = "skipEnd.svg", modifier = buttonModifier)
    }
}
