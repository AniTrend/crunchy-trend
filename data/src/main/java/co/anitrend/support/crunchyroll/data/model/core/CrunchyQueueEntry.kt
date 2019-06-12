package co.anitrend.support.crunchyroll.data.model.core

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia

@Entity
data class CrunchyQueueEntry(
    val last_watched_media: CrunchyMedia,
    val most_likely_media: CrunchyMedia,
    val ordering: Int,
    @PrimaryKey
    val queue_entry_id: Long,
    val last_watched_media_playhead: Int,
    val most_likely_media_playhead: Int,
    val playhead: Int,
    val series: CrunchySeries
)