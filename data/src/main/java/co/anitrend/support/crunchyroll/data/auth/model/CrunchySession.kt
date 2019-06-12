package co.anitrend.support.crunchyroll.data.auth.model

import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySessionUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser


data class CrunchySession(
    override val session_id: String,
    override val country_code: String,
    override val device_type: String,
    override val device_id: String,
    override val version: String,
    override val user: CrunchyUser,
    override val auth: String,
    override val expires: String
) : ICrunchySession, ICrunchySessionUser