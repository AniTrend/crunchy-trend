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

package co.anitrend.support.crunchyroll.feature.catalog

import co.anitrend.support.crunchyroll.feature.catalog.ui.fragment.CatalogContent
import co.anitrend.support.crunchyroll.navigation.Catalog
import co.anitrend.support.crunchyroll.navigation.contract.INavigationProvider

internal class FeatureProvider : Catalog.Provider {
    override fun fragment() =
        CatalogContent::class.java
}