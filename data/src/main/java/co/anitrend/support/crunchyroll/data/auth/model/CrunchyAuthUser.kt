package co.anitrend.support.crunchyroll.data.auth.model

import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySessionUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

data class CrunchyAuthUser(
    override val user: CrunchyUser,
    override val auth: String,
    override val expires: String
) : ICrunchySessionUser