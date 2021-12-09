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

package co.anitrend.support.crunchyroll.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.extra
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.commit
import co.anitrend.support.crunchyroll.databinding.ActivityMainBinding
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.navigation.extensions.forFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainScreen : CrunchyActivity<ActivityMainBinding>(), NavigationView.OnNavigationItemSelectedListener {

    private val bottomDrawerBehavior by lazy(UNSAFE) {
        BottomSheetBehavior.from(requireBinding().bottomNavigationDrawer)
    }

    @IdRes
    private var selectedItem: Int = R.id.nav_series_catalogue

    @StringRes
    private var selectedTitle: Int = R.string.nav_series_catalog

    private val presenter by inject<CrunchyCorePresenter>()

    private val params: Main.Payload? by extra(Main.extraKey)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(requireBinding().root)
        setSupportActionBar(requireBinding().bottomAppBar)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        selectedItem = when (params?.redirect) {
            Main.Payload.Nav.CATALOGUE -> R.id.nav_series_catalogue
            Main.Payload.Nav.DISCOVER -> R.id.nav_series_discover
            Main.Payload.Nav.LATEST -> R.id.nav_show_latest
            Main.Payload.Nav.NEWS -> R.id.nav_show_news
            else -> R.id.nav_series_catalogue
        }

    }

    /**
     * Additional initialization to be done in this method, if the overriding class is type of [SupportFragment]
     * then this method will be called in [SupportFragment.onCreate]. Otherwise [SupportActivity.onPostCreate]
     * invokes this function
     *
     * @see [SupportActivity.onPostCreate] and [SupportFragment.onCreate]
     * @param
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        requireBinding().floatingShortcutButton.setOnClickListener {
            bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        requireBinding().bottomNavigationView.apply {
            setNavigationItemSelectedListener(this@MainScreen)
            setCheckedItem(selectedItem)
        }
        updateUserInterface()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(keyNavigationSelected, selectedItem)
        outState.putInt(keyNavigationTitle, selectedTitle)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedItem = savedInstanceState.getInt(keyNavigationSelected)
        selectedTitle = savedInstanceState.getInt(keyNavigationTitle)
    }

    override fun onBackPressed() {
        when (bottomDrawerBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED,
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                bottomDrawerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }
            else -> {
                /** fixes leak discussed [here](https://issuetracker.google.com/issues/139738913) */
                finishAfterTransition()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                Search(applicationContext)
                return true
            }
            R.id.action_settings -> {
                Settings(applicationContext)
                return true
            }
            R.id.action_login -> {
                Authentication(applicationContext)
                if (!presenter.settings.isAuthenticated.value)
                    closeScreen()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (selectedItem != item.itemId) {
            selectedItem = item.itemId
            onNavigateToTarget(selectedItem)
        }
        return true
    }

    private suspend fun onNavigate(@IdRes menu: Int) {
        val fragmentItem = when (menu) {
            R.id.nav_series_catalogue -> {
                selectedTitle = R.string.nav_series_catalog
                FragmentItem(Catalog.forFragment())
            }
            R.id.nav_series_discover -> {
                selectedTitle = R.string.nav_discover
                FragmentItem(Discover.forFragment())
            }
            R.id.nav_show_latest -> {
                selectedTitle = R.string.nav_shows
                FragmentItem(Listing.forFragment())
            }
            R.id.nav_show_news -> {
                selectedTitle = R.string.nav_show_news
                FragmentItem(News.forFragment())
            }
            else -> null
        }

        requireBinding().bottomAppBar.setTitle(selectedTitle)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        delay(DEBOUNCE_DURATION)
        currentFragmentTag = fragmentItem?.commit(R.id.contentFrame, this) {}
    }

    private fun onNavigateToTarget(@IdRes menu: Int) {
        lifecycleScope.launch {
            runCatching {
                onNavigate(menu)
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    private fun updateUserInterface() {
        if (selectedItem != 0)
            onNavigateToTarget(selectedItem)
        else
            onNavigateToTarget(R.id.nav_show_latest)
    }

    companion object {
        private const val keyNavigationSelected = "keyNavigationSelected"
        private const val keyNavigationTitle = "keyNavigationTitle"
    }
}
