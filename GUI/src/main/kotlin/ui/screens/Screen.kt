/**
 * Represents the different screens the GUI application.
 * If a new screen is added, it should be added as a subclass of the Screen class
 */
sealed class Screen {
    /**
     * Represents the screen for selecting a video.
     *
     * This class extends the abstract class `Screen` to handle the specific functionalities
     * required for selecting a video.
     */
    object SelectVideoScreen : Screen()

    /**
     * The DiffScreen class represents the screen that displays two videos and their difference.
     * It is a subclass of the Screen class.
     *
     * @constructor Creates a new DiffScreen object.
     * @extends Screen
     */
    object DiffScreen : Screen()

    /**
     * The SettingsScreen class represents the screen that displays the settings.
     * It is a subclass of the Screen class.
     *
     * @constructor Creates a new SettingsScreen object.
     * @extends Screen
     */
    object SettingsScreen : Screen()
}
