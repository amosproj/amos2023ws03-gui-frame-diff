package ui.components.diffScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import frameNavigation.FrameNavigation
import ui.components.general.SvgButton

/**
 * A Composable function that creates a row of navigation buttons.
 *
 * @param navigator The FrameNavigation object that contains the navigation logic.
 * @param buttonModifier The modifier to be applied to the buttons.
 * @param rowModifier The modifier to be applied to the row.
 * @return [Unit]
 */
@Composable
fun NavigationButtons(
    navigator: FrameNavigation,
    buttonModifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
) {
    Row(modifier = rowModifier.fillMaxWidth()) {
        SvgButton(onClick = { navigator.jumpToNextDiff(false) }, content = "skipStart.svg", modifier = buttonModifier)
        SvgButton(onClick = { navigator.jumpFrames(-1) }, content = "skipPrev.svg", modifier = buttonModifier)
        SvgButton(onClick = { navigator.jumpFrames(1) }, content = "skipNext.svg", modifier = buttonModifier)
        SvgButton(onClick = { navigator.jumpToNextDiff(true) }, content = "skipEnd.svg", modifier = buttonModifier)
    }
}
