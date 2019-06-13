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

package co.anitrend.support.crunchyroll.data.dao.view.collection

import androidx.room.DatabaseView
import androidx.room.Embedded
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia

@DatabaseView("""
    select c.*, m.*
    from CrunchyCollection c
    join CrunchyMedia m on (c.collection_id = m.collection_id)
""")
data class CollectionWithMedia(
    @Embedded
    val crunchyCollection: CrunchyCollection,
    @Embedded
    val crunchyMedia: List<CrunchyMedia>
)