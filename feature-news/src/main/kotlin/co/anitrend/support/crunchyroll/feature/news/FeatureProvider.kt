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

package co.anitrend.support.crunchyroll.feature.news

import android.content.Context
import android.content.Intent
import co.anitrend.support.crunchyroll.feature.news.ui.activity.NewsScreen
import co.anitrend.support.crunchyroll.feature.news.ui.fragment.NewsFeedContent
import co.anitrend.support.crunchyroll.navigation.News
import co.anitrend.support.crunchyroll.navigation.contract.INavigationProvider

internal class FeatureProvider : News.Provider {
    override fun activity(context: Context?) =
        Intent(context, NewsScreen::class.java)

    override fun fragment() =
        NewsFeedContent::class.java
}