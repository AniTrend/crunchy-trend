package co.anitrend.support.crunchyroll.data.auth.model.contract

interface ICrunchySession {
    val session_id: String
    val country_code: String
    val device_type: String
    val device_id: String
    val version: String
}