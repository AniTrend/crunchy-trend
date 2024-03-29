/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.batch.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.batch.entity.CrunchyBatchEntity
import co.anitrend.support.crunchyroll.data.batch.model.CrunchyBatchModel
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel

internal class BatchTransformer : ISupportTransformer<List<CrunchyBatchModel>, List<CrunchyBatchEntity<CrunchySeriesModel>>> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source:  List<CrunchyBatchModel>): List<CrunchyBatchEntity<CrunchySeriesModel>> {
        return source.map {
            val contents = it.body.data.orEmpty()
            CrunchyBatchEntity(
                data = contents
            )
        }
    }
}