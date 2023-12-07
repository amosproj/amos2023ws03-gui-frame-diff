
/**
 * Provides navigation methods for frames in a video.
 *
 * This interface defines methods to navigate frames in various ways, including jumping a specified number
 * of frames in a given direction, jumping to a specific percentage of the video, jumping to a specific frame,
 * and jumping to the next difference frame. Implementing classes should provide implementation for these methods.
 */
abstract class AbstractFrameNavigationInterface {

    /**
     * Jump n frames in a specified direction
     */
    abstract fun jumpFrames(frames: Int, direction: Boolean)

    /**
     * Jump to a specified percentage of the diff video
     */
    abstract fun jumpToPercentage(percentage: Double)

    /**
     * Jump to a concretely specified frame
     */
    abstract fun jumpToFrame(index: Int)

    /**
     * Jump to the next diff
     */
    abstract fun jumpToNextDiff(direction: Boolean)
}
