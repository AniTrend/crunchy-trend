package co.anitrend.support.crunchyroll.data.arch.extension

import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.api.provider.EndpointProvider
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OfflineStrategy
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineStrategy
import co.anitrend.support.crunchyroll.data.arch.database.common.ICrunchyDatabase
import org.koin.core.scope.Scope

/**
 * Facade for supplying database contract
 */
internal fun Scope.db() = get<ICrunchyDatabase>()

internal inline fun <reified T> Scope.api(endpointType: EndpointType): T =
    EndpointProvider.provideRetrofit(endpointType, this).create(T::class.java)

/**
 * Facade for supplying online strategy
 */
internal fun <T> Scope.online() =
    OnlineStrategy.create<T>(
        connectivity = get()
    )

/**
 * Facade for supplying offline strategy
 */
internal fun <T> Scope.offline() =
    OfflineStrategy.create<T>()