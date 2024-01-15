package ui
import Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import models.AppState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ui.screens.SelectVideoScreen

/**
 * This class tests the [SelectVideoScreen].
 */
class SelectVideoScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * This function is called before each test and sets up the [SelectVideoScreen].
     */
    @Before
    fun setUp() {
        composeTestRule.setContent {
            var screenState by remember { mutableStateOf<Screen>(Screen.SelectVideoScreen) }

            when (screenState) {
                Screen.SelectVideoScreen ->
                    SelectVideoScreen(
                        state = remember { mutableStateOf(AppState()) },
                    )
                else -> {
                    SelectVideoScreen(
                        state = remember { mutableStateOf(AppState()) },
                    )
                }
            }
        }
    }

    /**
     * Test if the buttons are displayed
     */
    @Test
    fun `test presence of buttons`() {
        composeTestRule.onNodeWithText("Select Video 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select Video 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Compute and Display Differences").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("?").assertIsDisplayed()
    }

    /**
     * Test if Help Drop Down is displayed correctly
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test presence of dropdown menu`() =
        runTest { // runTest allows to use suspend functions
            composeTestRule.onNodeWithText("?").performClick()
            composeTestRule.awaitIdle() // suspend function
            composeTestRule.onNodeWithText("Project Page").assertIsDisplayed()
            composeTestRule.onNodeWithText("Help").assertIsDisplayed()
        }
}
