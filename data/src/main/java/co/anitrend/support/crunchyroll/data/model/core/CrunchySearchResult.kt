package co.anitrend.support.crunchyroll.data.model.core

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CrunchySearchResult(
    @PrimaryKey
    val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val portraitImage: String,
    val landscapeImage: String
)