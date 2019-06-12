package co.anitrend.support.crunchyroll.data.model.series.contract

import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet

interface ICrunchySeries {

    val media_type: CrunchyMediaType
    val series_id: String
    val name: String
    val description: String
    val url: String
    val media_count: Int?
    val landscape_image: CrunchyImageSet?
    val portrait_image: CrunchyImageSet?
}