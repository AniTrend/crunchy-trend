package co.anitrend.support.crunchyroll.data.source.session

import androidx.annotation.StringDef

@StringDef(
    SessionRequestType.START_UNBLOCK_SESSION,
    SessionRequestType.START_CORE_SESSION
)
annotation class SessionRequestType {
    companion object {
        const val START_UNBLOCK_SESSION = "START_UNBLOCK_SESSION"
        const val START_CORE_SESSION = "START_CORE_SESSION"
    }
}