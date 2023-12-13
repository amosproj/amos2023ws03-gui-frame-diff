package models

import Screen
import algorithms.AlignmentElement

data class AppState(
    var screen: Screen = Screen.SelectVideoScreen,
    var pathObj: AllVideos<String> = AllVideos("", "", ""),
    var sequenceObj: Array<AlignmentElement> = arrayOf<AlignmentElement>(),
)
