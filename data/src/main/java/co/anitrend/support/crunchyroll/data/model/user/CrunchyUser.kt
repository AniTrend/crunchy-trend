package co.anitrend.support.crunchyroll.data.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.AccessType

@Entity
data class CrunchyUser(
    @PrimaryKey
    val user_id: Long,
    val username: String,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val premium: String?,
    val access_type: AccessType?
)