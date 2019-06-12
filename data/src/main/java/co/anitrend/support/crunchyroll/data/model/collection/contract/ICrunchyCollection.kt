package co.anitrend.support.crunchyroll.data.model.collection.contract

import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet

interface ICrunchyCollection {

    val availability_notes: String
    val media_type: CrunchyMediaType
    val series_id: String
    val collection_id: String
    val complete: Boolean
    val name: String
    val description: String
    val landscape_image: CrunchyImageSet?
    val portrait_image: CrunchyImageSet?
    val season: String
    val created: String
}