package co.anitrend.support.crunchyroll.data.model.media

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet

@Entity
data class CrunchyMedia(
    val media_id: String,
    @PrimaryKey
    val collection_id: String,
    val collection_etp_guid: String,
    val series_id: String,
    val series_name: String,
    val series_etp_guid: String,
    val media_type: CrunchyMediaType,
    val episode_number: String,
    val duration: Int,
    val name: String,
    val description: String,
    val screenshot_image: CrunchyImageSet,
    val bif_url: String,
    val url: String,
    val clip: Boolean,
    val available: Boolean,
    val premium_available: Boolean,
    val free_available: Boolean,
    val availability_notes: String,
    val available_time: String,
    val unavailable_time: String,
    val premium_available_time: String,
    val premium_unavailable_time: String,
    val free_available_time: String,
    val free_unavailable_time: String,
    val created: String,
    val playhead: Int
) {

    val actualEpisodeNumber: Int
        get() {
            val regex = Regex("[^\\d.]")
            val filtered = episode_number.replace(regex, "")
            return filtered.toInt()
        }

    fun isShorterThanFiveMinutes() = duration < 300

    fun isFullEpisode() = actualEpisodeNumber % 1 == 0

    fun isSpecialEpisode() =
        episode_number == "SP" || episode_number.isBlank()

    fun qualifiesAsEpisode(): Boolean {
        return !isShorterThanFiveMinutes() ||
                isFullEpisode() ||
                !isSpecialEpisode()
    }
}