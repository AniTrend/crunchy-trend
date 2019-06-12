package co.anitrend.support.crunchyroll.data.model.core

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CrunchyLocale(
    @PrimaryKey
    val locale_id: String,
    val label: String
)