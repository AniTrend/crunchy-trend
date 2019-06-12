package co.anitrend.support.crunchyroll.data.entity.auth

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

@Entity(
    indices = [
        Index(value = ["userId"], unique = true),
        Index(value = ["auth"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = CrunchyUser::class,
            parentColumns = ["user_id"],
            childColumns = ["userId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class CrunchyAuthenticationWithUser(
    @PrimaryKey(autoGenerate = true)
    val authenticationId: Long = 0,
    val userId: Long,
    val auth: String,
    val expires: String
)