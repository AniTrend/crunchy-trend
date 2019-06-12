package co.anitrend.support.crunchyroll.data.model.stream

import co.anitrend.support.crunchyroll.data.arch.StreamQuality

data class CrunchyStream(
    val quality: StreamQuality,
    val expires: String,
    val url: String
)
