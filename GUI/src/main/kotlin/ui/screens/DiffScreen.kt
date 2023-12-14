package ui.screens
import algorithms.AlignmentElement
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPosition.PlatformDefault.x
import frameNavigation.FrameNavigation
import models.AllVideos
import ui.components.AutoSizeText

/**
 * A Composable function that creates a screen to display the differences between two videos.
 * @param paths [AllVideos] object containing the paths to the videos.
 * @param sequence [Array] of [AlignmentElement]s containing the alignment sequence.
 * @return [Unit]
 */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DiffScreen(
    paths: AllVideos<String>,
    sequence: Array<AlignmentElement>,
) {
    // create the navigator, which implements the jumping logic
    val navigator = FrameNavigation(paths, sequence)
    // force into focus to intercept key presses
    val focusRequester = remember { FocusRequester() }
    // map of key presses to actions
    val keyActions =
        mapOf(
            Pair(false, Key.DirectionRight) to { navigator.jumpFrames(1) },
            Pair(false, Key.DirectionLeft) to { navigator.jumpFrames(-1) },
            Pair(true, Key.DirectionRight) to { navigator.jumpToNextDiff(true) },
            Pair(true, Key.DirectionLeft) to { navigator.jumpToNextDiff(false) },
        )
    // create the screen
    Column(
        // grab focus, fill all available space, assign key press handler
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> keyEventHandler(event, keyActions) },
    ) {
//        ###########   Focus   ###########
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

//        ###########   Text   ###########
        Row(modifier = Modifier.fillMaxWidth().weight(0.2f)) {
            Title(text = "Video 1")
            Title(text = "Diff")
            Title(text = "Video 2")
        }
//        ###########   Box   ###########
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.6f)) {
            DisplayedImage(bitmap = navigator.video1Bitmap)
            DisplayedImage(bitmap = navigator.diffBitmap)
            DisplayedImage(bitmap = navigator.video2Bitmap)
        }
//        ###########   Timeline   ###########

        Row(modifier = Modifier.fillMaxWidth().weight(0.2f).background(color = Color.Gray), horizontalArrangement = Arrangement.Center) {
            timeline(navigator)
        }
//        ###########   Buttons   ###########
        Row(modifier = Modifier.fillMaxWidth().weight(0.2f)) {
            jumpButton(onClick = { navigator.jumpToNextDiff(false) }, content = "skipStart.svg")
            jumpButton(onClick = { navigator.jumpFrames(-1) }, content = "skipPrev.svg")
            jumpButton(onClick = { navigator.jumpFrames(1) }, content = "skipNext.svg")
            jumpButton(onClick = { navigator.jumpToNextDiff(true) }, content = "skipEnd.svg")
        }
    }
}

@Composable
private fun timeline(navigator: FrameNavigation) {
    var clickPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    var componentWidth by remember { mutableStateOf(0f) }
    Column(modifier = Modifier.background(color = Color.Gray)) {
        Box(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(
                text = "0",
                fontSize = 22.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 2.dp),
            )
            Text(
                text = "${navigator.getSizeOfDiff()}",
                fontSize = 22.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 2.dp),
            )
        }
        Box(
            modifier =
                Modifier
                    .background(color = Color.LightGray)
//                .fillMaxWidth(fraction = 0.8f)
                    .fillMaxWidth(0.8f)
                    .height(100.dp)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                    )
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        // Store the width
                        componentWidth =
                            with(density) {
                                placeable.width.toFloat()
                            }
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
//                    .align(Alignment.CenterHorizontally)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            clickPosition = offset
                            println(clickPosition.x.toDouble() / 6)
                            navigator.jumpToPercentage(clickPosition.x.toDouble() / componentWidth * 100)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                clickPosition = offset
                            },
                            onDrag = { change, dragAmount ->
                                clickPosition =
                                    clickPosition.copy(
                                        x = (clickPosition.x + dragAmount.x).coerceIn(0f, componentWidth),
                                    )
                                navigator.jumpToPercentage(clickPosition.x.toDouble() / componentWidth * 100)
                            },
                        )
                    },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    start = Offset(clickPosition.x, 0f),
                    end = Offset(clickPosition.x, size.height),
                    color = Color.Red,
                    strokeWidth = 6f,
                )
            }
            Text(
                text = "0%",
                color = Color.Black,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart).padding(20.dp),
            )

            val currentPercentage = (clickPosition.x / componentWidth * 100).toInt()

            Text(
                text = "$currentPercentage %",
                color = Color.Black,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center).padding(2.dp),
            )
            Text(
                text = "100%",
                color = Color.Black,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterEnd).padding(20.dp),
            )
        }
    }
}

/**
 * A Composable function that creates a box to display text.
 * @param text [String] containing the text to be displayed.
 * @return [Unit]
 */

@Composable
fun RowScope.Title(text: String) {
    AutoSizeText(
        text = text,
        modifier = Modifier.weight(1f).fillMaxSize().padding(20.dp),
    )
}

/**
 * A Composable function that creates a box to display an image.
 * @param bitmap [MutableState] of [ImageBitmap] containing the bitmap to be displayed.
 * @param modifier [Modifier] to be applied to the [Box].
 * @return [Unit]
 */
@Composable
fun RowScope.DisplayedImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.weight(1f).background(Color.Gray).padding(8.dp).fillMaxSize(1f),
        contentAlignment = Alignment.Center,
    ) {
        Image(bitmap = bitmap.value, null)
    }
}

/**
 * A Composable function that creates a button that jumps frames.
 * @param onClick [Function] to be called when the button is clicked.
 * @param content [String] containing the name of the svg file to be displayed.
 * @return [Unit]
 */
@Composable
fun RowScope.jumpButton(
    onClick: () -> Unit,
    content: String,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).padding(40.dp, 20.dp, 40.dp, 20.dp),
    ) {
        Image(
            painter = painterResource(content),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * A function that handles key presses.
 * @param event [KeyEvent] containing the key press event.
 * @param keyActions [Map] of [Pair]s of [Boolean] and [Key] to [Function]s to be called when the key is pressed.
 */
fun keyEventHandler(
    event: KeyEvent,
    keyActions: Map<Pair<Boolean, Key>, () -> Unit>,
): Boolean {
    // only handle key down events
    if (event.type != KeyEventType.KeyDown) return false
    // if the key is in the map, call the function
    val keyAction = keyActions[Pair(event.isCtrlPressed, event.key)]?.invoke()
    // return true if the key was in the map
    return keyAction != null
}
