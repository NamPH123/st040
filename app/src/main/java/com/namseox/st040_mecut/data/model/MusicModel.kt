package com.namseox.st040_mecut.data.model

import com.namseox.st040_mecut.utils.getVideoDuration
import java.io.File

data class MusicModel(
    var path: String,
    var check: Boolean = false,
    var checkPlaying: Boolean = false,
) {
    var file = File(path)
    var name: String = file.name
    var time: String = getVideoDuration(path)
}