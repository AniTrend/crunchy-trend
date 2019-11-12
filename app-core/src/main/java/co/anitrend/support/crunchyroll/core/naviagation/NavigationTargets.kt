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

package co.anitrend.support.crunchyroll.core.naviagation

import android.content.Context
import android.os.Parcelable
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationRouter
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationTarget
import co.anitrend.support.crunchyroll.core.naviagation.extensions.forIntent
import kotlinx.android.parcel.Parcelize

object NavigationTargets {

    object Main : INavigationRouter, INavigationTarget {
        override val packageName = "ui.activity"
        override val className = "MainScreen"

        override val navRouterIntent = forIntent()
    }

    object Splash : INavigationRouter, INavigationTarget {
        override val packageName = "feature.ui.activity"
        override val className = "SplashScreen"

        override val navRouterIntent = forIntent()
    }

    object Settings : INavigationRouter, INavigationTarget {
        override val packageName = "feature.settings.ui.activity"
        override val className = "SettingsScreen"

        override val navRouterIntent = forIntent()
    }

    object Authentication : INavigationRouter, INavigationTarget {
        override val packageName = "feature.authentication.ui.activity"
        override val className = "AuthenticationScreen"

        override val navRouterIntent = forIntent()
    }

    object News : INavigationRouter, INavigationTarget {
        override val packageName = "feature.news.ui.fragment"
        override val className = "NewsFeedContent"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "NewsFeedContent:Payload"
    }

    object Listing : INavigationRouter, INavigationTarget {
        override val packageName = "feature.listing.ui.fragment"
        override val className = "MediaFeedContent"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "MediaFeedContent:Payload"
    }

    object MediaDetail : INavigationRouter, INavigationTarget {
        override val packageName = "feature.media.ui.activity"
        override val className = "MediaDetailScreen"

        override val navRouterIntent = forIntent()
    }

    object MediaPlayer : INavigationRouter, INavigationTarget {
        override val packageName = "feature.player.ui.activity"
        override val className = "MediaPlayerScreen"

        override val navRouterIntent = forIntent()

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(context: Context?, payload: Payload) {
            navRouterIntent?.putExtra(PAYLOAD, payload)
            super.invoke(context)
        }

        const val PAYLOAD = "MediaStreamContent:Payload"

        @Parcelize
        data class Payload(
            val mediaId: Int,
            val episodeThumbnail: String?
        ) : Parcelable
    }

    object Search : INavigationRouter, INavigationTarget {
        override val packageName = "feature.search.ui.activity"
        override val className = "SearchScreen"

        override val navRouterIntent = forIntent()
    }
}