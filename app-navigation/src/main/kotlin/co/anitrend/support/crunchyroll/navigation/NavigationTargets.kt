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

package co.anitrend.support.crunchyroll.navigation

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import co.anitrend.support.crunchyroll.navigation.NavigationTargets.MediaPlayer.navRouterIntent
import co.anitrend.support.crunchyroll.navigation.contract.INavigationRouter
import co.anitrend.support.crunchyroll.navigation.contract.INavigationTarget
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import co.anitrend.support.crunchyroll.navigation.extensions.APPLICATION_PACKAGE_NAME
import co.anitrend.support.crunchyroll.navigation.extensions.forIntent
import co.anitrend.support.crunchyroll.navigation.extensions.loadClassOrNull
import kotlinx.android.parcel.Parcelize

object NavigationTargets {

    object Main : INavigationRouter, INavigationTarget {
        override val packageName = "ui"
        override val className = "MainScreen"

        override val navRouterIntent = forIntent()

        fun <T> koinInitializer(): Class<out T>? {
            val classPath = "$APPLICATION_PACKAGE_NAME.initializer.KoinInitializer"
            return classPath.loadClassOrNull()
        }
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

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val id: Long,
            val title: String,
            val subTitle: String,
            val description: String?,
            val content: String,
            val publishDate: Long?
        ) : Parcelable
    }

    object Listing : INavigationRouter, INavigationTarget {
        override val packageName = "feature.listing.ui.fragment"
        override val className = "MediaFeedContent"

        override val navRouterIntent = forIntent()
    }

    object Media : INavigationRouter, INavigationTarget {
        override val packageName = "feature.media.ui.activity"
        override val className = "MediaScreen"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "MediaContent:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val collectionThumbnail: String?,
            val collectionName: String,
            val collectionId: Long
        ) : Parcelable
    }

    object MediaPlayer : INavigationRouter, INavigationTarget {
        override val packageName = "feature.player.ui.activity"
        override val className = "MediaPlayerScreen"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "MediaStreamContent:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val mediaId: Long,
            val collectionName: String?,
            val collectionThumbnail: String?,
            val episodeTitle: String?,
            val episodeThumbnail: String?
        ) : Parcelable
    }

    object Search : INavigationRouter, INavigationTarget {
        override val packageName = "feature.search.ui.activity"
        override val className = "SearchScreen"

        override val navRouterIntent = forIntent()
    }

    object DiscoverScreen : INavigationRouter, INavigationTarget {
        override val packageName = "feature.discover.ui.activity"
        override val className = "SeriesDiscoverScreen"

        override val navRouterIntent = forIntent()

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Discover.Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                Discover.PAYLOAD, payload)
            super.invoke(context, options)
        }
    }

    object Discover : INavigationRouter, INavigationTarget {
        override val packageName = "feature.discover.ui.fragment"
        override val className = "SeriesDiscoverContent"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "SeriesDiscoverContent:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(context: Context?, payload: Payload, options: Bundle? = null) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val browseFilter: CrunchySeriesBrowseFilter,
            val filterOption: String = ""
        ) : Parcelable
    }

    object Catalog : INavigationRouter, INavigationTarget {
        override val packageName = "feature.catalog.ui.fragment"
        override val className = "CatalogContent"

        override val navRouterIntent = forIntent()
    }

    object Series : INavigationRouter, INavigationTarget {
        override val packageName = "feature.series.ui.activity"
        override val className = "SeriesScreen"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "SeriesScreen:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val seriesId: Long
        ) : Parcelable
    }

    object Collection : INavigationRouter, INavigationTarget {
        override val packageName = "feature.collection.ui.activity"
        override val className = "CollectionScreen"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "CollectionScreen:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val seriesId: Long
        ) : Parcelable
    }

    object ImageViewer : INavigationRouter, INavigationTarget {
        override val packageName = "shared.image.viewer"
        override val className = "ImageViewerScreen"

        override val navRouterIntent = forIntent()

        const val PAYLOAD = "ImageViewerScreen:Payload"

        /**
         * Starts the target [navRouterIntent] for the implementation
         */
        operator fun invoke(
            context: Context?,
            payload: Payload,
            options: Bundle? = null
        ) {
            navRouterIntent?.putExtra(
                PAYLOAD, payload)
            super.invoke(context, options)
        }

        @Parcelize
        data class Payload(
            val imageSrc: String
        ) : Parcelable
    }
}