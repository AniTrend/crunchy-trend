/*
 *    Copyright 2019 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.session.source

import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.mapper.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.session.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class CoreSessionSourceImpl(
    private val settings: IAuthenticationSettings,
    private val dao: CrunchySessionCoreDao,
    private val endpoint: CrunchySessionEndpoint,
    private val responseMapper: CoreSessionResponseMapper
) : SessionSource() {

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override fun invoke(): Session? {
        super.invoke()
        networkState.postValue(NetworkState.Loading)
        
        val deferred = async {
            endpoint.startCoreSession()
        }

        val session = runBlocking {
            responseMapper(deferred, networkState)
        }
        if (session != null)
            settings.sessionId = session.sessionId

        return CoreSessionTransformer.transform(session)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}