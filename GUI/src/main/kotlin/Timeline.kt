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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.AutoSizeText

class Timeline {
    /**
     * A Composable function that creates a box to display the timeline.
     * @param navigator [FrameNavigation] containing the navigator.
     * @return [Unit]
     */
    @Composable
    fun timeline(navigator: FrameNavigation) {
//        coordinates of the clicked position in the timelinebox
        var indicatorPosition by remember { mutableStateOf(Offset.Zero) }
//        set the width of the timelinebox
        var componentWidth by remember { mutableStateOf(0.8f) }
//        current percentage on the cursor
        val currentPercentage = (indicatorPosition.x / componentWidth * 100).toInt()
        Column(
            modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.3f)) {
                Box {
                }
                AutoSizeText(
                    text = "0",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 2.dp),
                )
                AutoSizeText(
                    text = "$currentPercentage%",
                    color = Color.Black,
                    modifier = Modifier.offset(x = ((indicatorPosition.x) - 1).dp),
                )
                AutoSizeText(
                    text = "${navigator.getSizeOfDiff()}",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 2.dp),
                )
            }
//            #### timeline box ####
            var helper = Offset.Zero
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
                                val percent = navigator.jumpToPercentage(offset.x.toDouble() / componentWidth)
                                indicatorPosition = indicatorPosition.copy(x = percent.toFloat() * componentWidth)
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    helper = offset
                                    val percent = navigator.jumpToPercentage(offset.x.toDouble() / componentWidth)
                                    indicatorPosition = indicatorPosition.copy(x = percent.toFloat() * componentWidth)
                                },
                                onDrag = { _, dragAmount ->
                                    helper = helper.copy(x = helper.x + dragAmount.x)
                                    val percent = navigator.jumpToPercentage(helper.x.toDouble() / componentWidth)
                                    indicatorPosition = indicatorPosition.copy(x = percent.toFloat() * componentWidth)
                                },
//                            onDragEnd = {
//                                val percent = navigator.jumpToPercentage(indicatorPosition.x.toDouble() / componentWidth)
//                                indicatorPosition = indicatorPosition.copy(x = percent.toFloat() * componentWidth)
//                            },
                            )
                        },
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        start = Offset(indicatorPosition.x, 0f),
                        end = Offset(indicatorPosition.x, size.height),
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
