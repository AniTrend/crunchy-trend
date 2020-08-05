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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.ext.extra
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.extensions.commit
import co.anitrend.support.crunchyroll.navigation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.fragment.model.FragmentItem
import co.anitrend.support.crunchyroll.navigation.extensions.forFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainScreen : CrunchyActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val bottomDrawerBehavior by lazy(LAZY_MODE_UNSAFE) {
        BottomSheetBehavior.from(bottomNavigationDrawer)
    }

    private val shortcutSelection by extra(keyShortcutRedirect, R.id.nav_show_news)

    @IdRes
    private var selectedItem: Int = R.id.nav_show_latest

    @StringRes
    private var selectedTitle: Int = R.string.nav_show_news

    private val presenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        shortcutSelection?.also {
            selectedItem = it
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
        floatingShortcutButton.setOnClickListener {
            bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        bottomNavigationView.apply {
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
            else -> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                NavigationTargets.Search(applicationContext)
                return true
            }
            R.id.action_settings -> {
                NavigationTargets.Settings(applicationContext)
                return true
            }
            R.id.action_login -> {
                NavigationTargets.Authentication(applicationContext)
                if (!presenter.settings.isAuthenticated)
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
            R.id.nav_series_catalog -> {
                selectedTitle = R.string.nav_series_catalog
                NavigationTargets
                    .Catalog.forFragment<Fragment>()
                    ?.let {
                        FragmentItem(
                            fragment = it
                        )
                }

            }
            R.id.nav_series_discover -> {
                selectedTitle = R.string.nav_discover
                NavigationTargets
                    .Discover.forFragment<Fragment>()
                    ?.let {
                        FragmentItem(
                            fragment = it
                        )
                    }
            }
            R.id.nav_show_latest -> {
                selectedTitle = R.string.nav_shows
                NavigationTargets
                    .Listing.forFragment<Fragment>()
                    ?.let {
                        FragmentItem(
                            fragment = it
                        )
                    }
            }
            R.id.nav_show_news -> {
                selectedTitle = R.string.nav_show_news
                NavigationTargets
                    .News.forFragment<Fragment>()
                    ?.let {
                        FragmentItem(
                            fragment = it
                        )
                    }
            }
            else -> null
        }

        bottomAppBar.setTitle(selectedTitle)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        delay(DEBOUNCE_DURATION)
        currentFragmentTag = supportFragmentManager.commit(
            R.id.contentFrame, fragmentItem
        ) {}
    }

    private fun onNavigateToTarget(@IdRes menu: Int) {
        lifecycleScope.launch {
            runCatching {
                onNavigate(menu)
            }.onFailure {
                Timber.tag(moduleTag).e(it)
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

        private const val keyShortcutRedirect = "keyShortcutRedirect"
    }
}
