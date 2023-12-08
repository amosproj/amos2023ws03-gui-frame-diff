package ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

/**
 * A Composable function that creates a screen to display the differences between two videos.
 *
 * @return [Unit]
 */
@Composable
fun DiffScreen() {
    var fileNames by remember { mutableStateOf(emptyList<String>()) }
    var index by remember { mutableStateOf(0) }
    val resourceFolder = "/test/"

    val url = object {}.javaClass.getResource(resourceFolder)
    val folderPath: Path =
        if (url != null) {
            url.toURI().let { uri ->
                if (uri.scheme == "jar") {
                    FileSystems.newFileSystem(uri, emptyMap<String, Any>()).getPath(resourceFolder)
                } else {
                    Path.of(uri)
                }
            }
        } else {
            throw IllegalArgumentException("Resource $resourceFolder not found")
        }

    fileNames =
        Files.walk(folderPath, 1)
            .filter { Files.isRegularFile(it) }
            .map { folderPath.relativize(it).toString() }
            .sorted()
            .toList()

    MaterialTheme {
//        ###########   Text   ###########
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp, start = 50.dp),
        ) {
            Text(
                text = "Video 1",
                modifier =
                    Modifier
                        .background(Color.Gray)
                        .height(50.dp)
                        .width(500.dp)
                        .padding(top = 5.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.width(30.dp))

            Text(
                text = "Diff",
                modifier =
                    Modifier
                        .background(Color.Gray)
                        .height(50.dp)
                        .width(500.dp)
                        .padding(top = 5.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            )
            Spacer(modifier = Modifier.width(30.dp))

            Text(
                text = "Video 2",
                modifier =
                    Modifier
                        .background(Color.Gray)
                        .height(50.dp)
                        .width(500.dp)
                        .padding(top = 5.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1.copy(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            )
        }
//        ###########   Box   ###########
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp, start = 50.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier.width(500.dp).height(500.dp).background(Color.Gray),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource("${resourceFolder}${fileNames[index]}"), null)
            }

            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier.width(500.dp).height(500.dp).background(Color.Gray),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource("${resourceFolder}${fileNames[index]}"), null)
            }
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier.width(500.dp).height(500.dp).background(Color.Gray),
                contentAlignment = Alignment.Center,
            ) {
                Image(painterResource("${resourceFolder}${fileNames[index]}"), null)
            }
        }
//        ###########   Buttons   ###########
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 640.dp, start = 50.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Button(
                onClick = {
                    println("prev diff button clicked")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier =
                    Modifier
                        .height(60.dp)
                        .width(80.dp),
            ) {
                Image(
                    painter = painterResource("skipStart.svg"),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(50.dp),
                )
            }
            Spacer(modifier = Modifier.width(450.dp))
            Button(
                onClick = {
                    if (index > 0) {
                        index--
                    }
                    println("prev frame button clicked")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier =
                    Modifier
                        .height(60.dp)
                        .width(80.dp),
            ) {
                Image(
                    painter = painterResource("skipPrev.svg"),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(50.dp),
                )
            }
            Spacer(modifier = Modifier.width(340.dp))

            Button(
                onClick = {
                    if (index < fileNames.size - 1) {
                        index++
                    }
                    println("next frame button clicked")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier =
                    Modifier
                        .height(60.dp)
                        .width(80.dp),
            ) {
                Image(
                    painter = painterResource("skipNext.svg"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(modifier = Modifier.width(450.dp))
            Button(
                onClick = {
                    println("next diff button clicked")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                modifier =
                    Modifier
                        .height(60.dp)
                        .width(80.dp),
            ) {
                Image(
                    painter = painterResource("skipEnd.svg"),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(50.dp),
                )
            }
        }
    }
}
