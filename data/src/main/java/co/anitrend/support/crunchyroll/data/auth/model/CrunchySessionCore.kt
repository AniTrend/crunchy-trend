package co.anitrend.support.crunchyroll.data.auth.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySession

@Entity
data class CrunchySessionCore(
    @PrimaryKey(autoGenerate = true)
    val sessionCoreId: Long,
    override val session_id: String,
    override val country_code: String,
    override val device_type: String,
    override val device_id: String,
    override val version: String
) : ICrunchySession