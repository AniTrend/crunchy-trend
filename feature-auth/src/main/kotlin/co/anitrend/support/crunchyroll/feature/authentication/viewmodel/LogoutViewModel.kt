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

package co.anitrend.support.crunchyroll.feature.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import co.anitrend.arch.core.viewmodel.SupportViewModel
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.transformer.CrunchyUserTransformer
import co.anitrend.support.crunchyroll.data.authentication.usecase.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser

class LogoutViewModel(
    override val useCase: LogoutUseCaseImpl,
    private val loginDao: CrunchyLoginDao
) : SupportViewModel<Nothing?, Boolean>() {

    /**
     * Starts view model operations
     *
     * @param parameter request payload
     */
    override fun invoke(parameter: Nothing?) {
        val result = useCase(parameter)
        useCaseResult.value = result
    }

    fun getUserByIdLiveData(userId: Long): LiveData<CrunchyUser?> {
        val liveData = loginDao.findByUserIdX(userId)
        return Transformations.map(liveData) {
            CrunchyUserTransformer.transform(it)
        }
    }
}