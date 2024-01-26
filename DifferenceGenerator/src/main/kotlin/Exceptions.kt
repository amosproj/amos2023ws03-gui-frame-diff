

/**
 * Base class for all exceptions thrown by the library.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
open class DifferenceGeneratorException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    override fun toString(): String {
        return "DifferenceGeneratorException(message=$message, cause=$cause)"
    }
}

/**
 * Exception thrown when the video codec is not supported.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
class DifferenceGeneratorCodecException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "CodecException(message=$message, cause=$cause)"
    }
}

/**
 * Exception thrown when the video dimensions (width, height) don't match.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
class DifferenceGeneratorDimensionException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "DimensionException(message=$message, cause=$cause)"
    }
}

/**
 * Exception thrown when the video container is not supported.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
class DifferenceGeneratorContainerException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "ContainerException(message=$message, cause=$cause)"
    }
}

/**
 * Exception thrown when the mask is invalid.
 *
 * Different reasons could be dimensions and codec.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
class DifferenceGeneratorMaskException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "MaskException(message=$message, cause=$cause)"
    }
}

/**
 * Exception thrown when the algorithm run is stopped from the outside.
 *
 * @param message the detail message.
 * @param cause the cause.
 */
class DifferenceGeneratorStoppedException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "StoppedException(message=$message, cause=$cause)"
    }
}
