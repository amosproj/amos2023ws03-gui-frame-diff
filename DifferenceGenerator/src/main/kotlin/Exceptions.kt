

open class DifferenceGeneratorException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    override fun toString(): String {
        return "DifferenceGeneratorException(message=$message, cause=$cause)"
    }
}

class DifferenceGeneratorCodecException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "CodecException(message=$message, cause=$cause)"
    }
}

class DifferenceGeneratorDimensionException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "DimensionException(message=$message, cause=$cause)"
    }
}

class DifferenceGeneratorContainerException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "ContainerException(message=$message, cause=$cause)"
    }
}

class DifferenceGeneratorMaskException(message: String, cause: Throwable? = null) : DifferenceGeneratorException(message, cause) {
    override fun toString(): String {
        return "MaskException(message=$message, cause=$cause)"
    }
}
