package co.anitrend.support.crunchyroll.data.model.stream

data class CrunchyStreamData(
    val hardsub_lang: String,
    val audio_lang: String,
    val format: String,
    val streams: List<CrunchyStream>
)