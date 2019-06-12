package co.anitrend.support.crunchyroll.data.entity.series

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.entity.collection.CollectionWithEpisodes
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet
import co.anitrend.support.crunchyroll.data.model.series.contract.ICrunchySeries

@Entity(
    indices = [
        Index(value = ["series_id"], unique = true)
    ]
)
data class CrunchySeriesWithCollections(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    override val media_type: CrunchyMediaType,
    override val series_id: String,
    override val name: String,
    override val description: String,
    override val url: String,
    override val media_count: Int?,
    override val landscape_image: CrunchyImageSet?,
    override val portrait_image: CrunchyImageSet?,
    val collections: List<CollectionWithEpisodes>
) : ICrunchySeries