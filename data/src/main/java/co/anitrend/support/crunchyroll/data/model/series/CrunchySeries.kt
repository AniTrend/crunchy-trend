package co.anitrend.support.crunchyroll.data.model.series

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet
import co.anitrend.support.crunchyroll.data.model.series.contract.ICrunchySeries

@Entity
data class CrunchySeries(
    override val media_type: CrunchyMediaType,
    @PrimaryKey
    override val series_id: String,
    override val name: String,
    override val description: String,
    override val url: String,
    override val media_count: Int?,
    override val landscape_image: CrunchyImageSet?,
    override val portrait_image: CrunchyImageSet?
) : ICrunchySeries