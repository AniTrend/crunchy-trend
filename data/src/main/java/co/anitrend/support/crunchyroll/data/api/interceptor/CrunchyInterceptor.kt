package co.anitrend.support.crunchyroll.data.api.interceptor

import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import io.wax911.support.data.auth.contract.ISupportAuthentication
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.contract.SupportStateContract
import io.wax911.support.data.model.extension.isUnauthorized
import okhttp3.*
import java.io.IOException

/**
 * Authentication interceptor adds query parameters dynamically when the application is authenticated.
 * The context in which an [Interceptor] may be  parallel or asynchronous depending
 * on the dispatching caller, as such take care to assure thread safety
 */
class CrunchyInterceptor(
    private val authenticationHelper: CrunchyAuthenticationHelper
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = authenticationHelper.injectHeaders(
            request = original
        )

        return chain.proceed(requestBuilder.build())
    }

}
