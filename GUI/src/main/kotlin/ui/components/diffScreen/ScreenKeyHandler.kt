package ui.components.diffScreen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import frameNavigation.FrameNavigation

/**
 * A function that handles key presses.
 * @param event [KeyEvent] containing the key press event.
 * @param navigator The FrameNavigation object that contains the navigation logic.
 * @return [Boolean] true if the key was handled, false otherwise.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun keyEventHandler(
    event: KeyEvent,
    navigator: FrameNavigation,
): Boolean {
    // map of key presses to actions
    val keyActions =
        mapOf(
            Pair(false, Key.DirectionRight) to { navigator.jumpFrames(1) },
            Pair(false, Key.DirectionLeft) to { navigator.jumpFrames(-1) },
            Pair(true, Key.DirectionRight) to { navigator.jumpToNextDiff(true) },
            Pair(true, Key.DirectionLeft) to { navigator.jumpToNextDiff(false) },
        )
    // only handle key down events
    if (event.type != KeyEventType.KeyDown) return false
    // if the key is in the map, call the function
    val keyAction = keyActions[Pair(event.isCtrlPressed, event.key)]?.invoke()
    // return true if the key was in the map
    return keyAction != null
}
