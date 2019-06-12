package co.anitrend.support.crunchyroll.data.api.endpoint.contract

import co.anitrend.support.crunchyroll.data.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Generates retrofit service classes
 *
 * @param url The url to use when create a service endpoint
 * @param serviceClass The interface class method representing your request to use
 */
abstract class EndpointFactory<S>(
    url: String,
    private val serviceClass: Class<S>,
    private val injectInterceptor: Boolean = true
) : KoinComponent {

    private val clientInterceptor by inject<Interceptor>()

    private val gson: Gson by lazy {
        GsonBuilder()
            .generateNonExecutableJson()
            .serializeNulls()
            .setLenient()
            .create()
    }

    private val retrofit: Retrofit by lazy {
        val httpClient = OkHttpClient.Builder()
            .apply {
                if (injectInterceptor)
                    addInterceptor(clientInterceptor)
                readTimeout(35, TimeUnit.SECONDS)
                connectTimeout(35, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)
                when {
                    BuildConfig.DEBUG -> {
                        val httpLoggingInterceptor = HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
                        addInterceptor(httpLoggingInterceptor)
                    }
                }
            }.build()

        Retrofit.Builder().client(httpClient).apply {
            addConverterFactory(
                GsonConverterFactory.create(
                    gson
                )
            ).baseUrl(
                url
            )
        }.build()
    }

    fun createService(): S = retrofit.create(serviceClass)
}