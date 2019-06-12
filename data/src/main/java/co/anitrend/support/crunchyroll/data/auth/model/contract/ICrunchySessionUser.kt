package co.anitrend.support.crunchyroll.data.auth.model.contract

import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

interface ICrunchySessionUser {
    val user: CrunchyUser
    val auth: String
    val expires: String
}