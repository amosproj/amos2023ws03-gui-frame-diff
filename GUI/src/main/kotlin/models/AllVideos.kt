package models

/*
    A data class to simplify the transfer of video objects/attributes.
 */
data class AllVideos<T>(
    var video1: T,
    var video2: T,
    var diffVideo: T,
)
