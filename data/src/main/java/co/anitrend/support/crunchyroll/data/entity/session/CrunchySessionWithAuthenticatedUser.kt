package co.anitrend.support.crunchyroll.data.entity.session

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySession
import co.anitrend.support.crunchyroll.data.entity.auth.CrunchyAuthenticationWithUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

@Entity(
    indices = [
        Index(value = ["authenticationId"], unique = true),
        Index(value = ["userId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CrunchyUser::class,
            parentColumns = ["user_id"],
            childColumns = ["userId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CrunchyAuthenticationWithUser::class,
            parentColumns = ["authenticationId"],
            childColumns = ["authenticationId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CrunchySessionWithAuthenticatedUser(
    @PrimaryKey(autoGenerate = true)
    val sessionAuthenticationId: Long = 0,
    override val session_id: String,
    override val country_code: String,
    override val device_type: String,
    override val device_id: String,
    override val version: String,
    val authenticationId: Long,
    val expires: String,
    val userId: Long
) : ICrunchySession