package co.anitrend.support.crunchyroll.data.entity.collection

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.collection.contract.ICrunchyCollection
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet

@Entity(
    indices = [
        Index(value = ["series_id"]),
        Index(value = ["collection_id"])
    ]
)
data class CollectionWithEpisodes(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    override val availability_notes: String,
    override val media_type: CrunchyMediaType,
    override val series_id: String,
    override val collection_id: String,
    override val complete: Boolean,
    override val name: String,
    override val description: String,
    override val landscape_image: CrunchyImageSet?,
    override val portrait_image: CrunchyImageSet?,
    override val season: String,
    override val created: String
) : ICrunchyCollection