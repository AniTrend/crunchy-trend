package co.anitrend.support.crunchyroll.data.source.auth

import androidx.annotation.StringDef

@StringDef(
    AuthRequestType.LOG_IN,
    AuthRequestType.LOG_OUT
)
annotation class AuthRequestType {
    companion object {
        const val LOG_IN = "LOG_IN"
        const val LOG_OUT = "LOG_OUT"

        const val arg_account = "arg_account"
        const val arg_password = "arg_password"
    }
}