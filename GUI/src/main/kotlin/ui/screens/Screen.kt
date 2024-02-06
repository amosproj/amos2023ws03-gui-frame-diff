// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*

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

/**
 * The ScreenSerializer class is a singleton class that serializes a Screen object.
 *
 * @constructor Creates a new ScreenSerializer object.
 */
class ScreenSerializer : JsonSerializer<Screen>() {
    override fun serialize(
        value: Screen,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        when (value) {
            is Screen.SelectVideoScreen -> gen.writeString("SelectVideoScreen")
            is Screen.DiffScreen -> gen.writeString("DiffScreen")
            is Screen.SettingsScreen -> gen.writeString("SettingsScreen")
            else -> {
                throw IllegalArgumentException("Unknown Screen type")
            }
        }
    }
}

/**
 * The ScreenDeserializer class is a singleton class that deserializes a Screen object.
 * It is a subclass of the abstract class JsonDeserializer.
 */
class ScreenDeserializer : JsonDeserializer<Screen>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Screen {
        val node: JsonNode = p.codec.readTree(p)
        return when (node.textValue()) {
            "SelectVideoScreen" -> Screen.SelectVideoScreen
            "DiffScreen" -> Screen.DiffScreen
            "SettingsScreen" -> Screen.SettingsScreen
            else -> throw IllegalArgumentException("Invalid screen type")
        }
    }
}
