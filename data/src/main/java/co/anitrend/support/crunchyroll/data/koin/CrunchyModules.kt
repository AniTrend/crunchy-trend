package co.anitrend.support.crunchyroll.data.koin

import android.content.Context
import android.net.ConnectivityManager
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.*
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyInterceptor
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthenticationRepository
import co.anitrend.support.crunchyroll.data.repository.session.CrunchySessionRepository
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.extension.util.SupportConnectivityHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val crunchyModules = module {
    single {
        CrunchyDatabase.newInstance(
            context =  androidContext()
        )
    }
    factory {
        CrunchySettings(
            context = androidContext()
        )
    }
}

val crunchyNetworkModules = module {
    factory {
        SupportConnectivityHelper(
            androidContext().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager?
        )
    }
    factory {
        CrunchyAuthenticationHelper(
            connectivityHelper = get(),
            settings = get(),
            deviceToken = BuildConfig.clientToken,
            sessionWithAuthenticatedUser = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    single {
        CrunchyInterceptor(
            authenticationHelper = get()
        )
    }
}

val crunchyEndpointModules = module {
    factory {
        CrunchyAuthEndpoint.createService()
    }
    factory {
        CrunchyCollectionEndpoint.createService()
    }
    factory {
        CrunchyMediaEndpoint.createService()
    }
    factory {
        CrunchySeriesEndpoint.createService()
    }
    factory {
        CrunchySessionEndpoint.createService()
    }
}

val crunchyRepositoryModules = module {
    factory {
        CrunchyAuthenticationRepository(
            authEndpoint = get()
        )
    }
    factory {
        CrunchySessionRepository(
            authEndpoint = get(),
            sessionEndpoint = get()
        )
    }
}