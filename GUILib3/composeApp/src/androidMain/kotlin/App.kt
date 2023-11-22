import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello World!") }
        var showImage by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                greetingText = "Compose: ${Greeting().greet()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    null
                )
            }
            val skipPrev = Icons.Filled.SkipPrevious
            val playArrow = Icons.Filled.PlayArrow
            val pause = Icons.Filled.Pause
            val skipNext = Icons.Filled.SkipNext
            var isTextVisible by remember { mutableStateOf(false) }
            var isTextVisible1 by remember { mutableStateOf(false) }
            Box() {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Button(onClick = { isTextVisible = !isTextVisible }) {
                        Icon(imageVector = skipPrev, contentDescription = "skip previus")
                    }
                    Button(onClick = { isTextVisible1 = !isTextVisible1}) {
                        Icon(imageVector = playArrow, contentDescription = "skip previus")
                    }
                    Button(onClick = { /*TODO*/ }) {
                        Icon(imageVector = pause, contentDescription = "skip previus")
                    }
                    Button(onClick = { /*TODO*/ }) {
                        Icon(imageVector = skipNext, contentDescription = "skip previus")
                    }

                    Column(
                        Modifier.background(color = Color.Red),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isTextVisible) {
                            Text(
                                text = "text from this class", fontSize = 30.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Column(
                        Modifier.background(color = Color.Green),
                    ) {
                        if (isTextVisible1) {
                            Text(
                                text = Greeting().textFromAnotherClass, fontSize = 30.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            }
        }
    }
}