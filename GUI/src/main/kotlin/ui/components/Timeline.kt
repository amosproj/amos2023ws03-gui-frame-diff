package ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation

class Timeline {
    /**
     * A Composable function that creates a box to display the timeline.
     * @param navigator [FrameNavigation] containing the navigator.
     * @return [Unit]
     */
    @Composable
    fun timeline(navigator: FrameNavigation) {
        // set the width of the timelinebox
        var componentWidth by remember { mutableStateOf(0.8f) }

        // current percentage on the cursor as Int between 0 and 100
        val currentPercentage = (navigator.currentRelativePosition.value * 100).toInt()

        // current x-offset of the indicator
        val currentOffset = (navigator.currentRelativePosition.value * componentWidth).toFloat()
        // current x-offset of the indicator as dp to show current percentage
        val currentOffsetDp = with(LocalDensity.current) { currentOffset.toDp() }
        // width of text component to center the current percentage over the cursor
        var textWidth by remember { mutableStateOf(0f) }
        Column(
            modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.3f),
            ) {
                AutoSizeText(
                    text = "0",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 2.dp),
                )
                AutoSizeText(
                    text = "$currentPercentage%",
                    color = Color.Black,
                    modifier =
                        Modifier.onGloballyPositioned {
                                coordinates ->
                            textWidth = coordinates.size.width.toFloat()
                        }.offset(x = (currentOffsetDp - (textWidth / 3).dp)),
                )
                AutoSizeText(
                    text = "${navigator.getSizeOfDiff()}",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 2.dp),
                )
            }
//            #### timeline box ####
            var cursorOffset = Offset.Zero
            Box(
                modifier =
                    Modifier
                        .background(color = Color.LightGray)
                        .fillMaxWidth(0.8f)
                        .height(100.dp)
                        .weight(1f)
                        .border(
                            width = 2.dp,
                            color = Color.Black,
                        )
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            // Store the width
                            componentWidth =
                                placeable.width.toFloat()
                            layout(placeable.width, placeable.height) {
                                placeable.placeRelative(0, 0)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                println("offset: $currentOffset")
                                println("componenWidth: $componentWidth")
                                navigator.jumpToPercentage(offset.x.toDouble() / componentWidth)
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    cursorOffset = offset
                                    navigator.jumpToPercentage(offset.x.toDouble() / componentWidth)
                                },
                                onDrag = { _, dragAmount ->
                                    cursorOffset = cursorOffset.copy(x = cursorOffset.x + dragAmount.x)
                                    navigator.jumpToPercentage((cursorOffset.x.toDouble() / componentWidth).coerceIn(0.0, 1.0))
                                },
                            )
                        },
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        start = Offset(currentOffset, 0f),
                        end = Offset(currentOffset, size.height),
                        color = Color.Red,
                        strokeWidth = 6f,
                    )
                }
//                #### percentage row ####
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        AutoSizeText(
                            text = "0%",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.5f).background(color = Color.Red))

                    Box {
                        AutoSizeText(
                            text = "50%",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                        )
                    }
                    Spacer(
                        modifier =
                            Modifier
                                .weight(0.5f),
                    )
                    Box {
                        AutoSizeText(
                            text = "100%",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                        )
                    }
                }
            }
        }
    }
}
