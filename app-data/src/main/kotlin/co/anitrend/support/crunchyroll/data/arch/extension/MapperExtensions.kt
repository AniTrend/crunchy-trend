package co.anitrend.support.crunchyroll.data.arch.extension

import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.data.arch.controller.core.DefaultController
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.arch.network.default.DefaultNetworkClient
import org.koin.core.scope.Scope

/**
 * Extension to help us create a controller
 */
internal fun <S, D> Scope.defaultController(
    mapper: DefaultMapper<S, D>,
    strategy: ControllerStrategy<D> = online(),
    dispatcher: ISupportDispatcher = get()
) = DefaultController(
    mapper = mapper,
    strategy = strategy,
    dispatcher = dispatcher.io,
    client = DefaultNetworkClient(
        dispatcher = dispatcher.io
    )
)