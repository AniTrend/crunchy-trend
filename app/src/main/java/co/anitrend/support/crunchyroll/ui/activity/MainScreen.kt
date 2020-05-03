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

package co.anitrend.support.crunchyroll.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.extra
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.arch.ui.common.ISupportActionUp
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.naviagation.extensions.forFragment
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainScreen : CrunchyActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val bottomDrawerBehavior by lazy(LAZY_MODE_UNSAFE) {
        BottomSheetBehavior.from(bottomNavigationDrawer)
    }

    private val shortcutSelection by extra(keyShortcutRedirect, R.id.nav_show_news)

    @IdRes
    private var selectedItem: Int = R.id.nav_show_latest

    @StringRes
    private var selectedTitle: Int = R.string.nav_shows


    override val elasticLayout: ElasticDragDismissFrameLayout? = null

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
        onUpdateUserInterface()
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
            launch {
                onNavigate(selectedItem)
            }
        }
        return true
    }

    private fun onNavigate(@IdRes menu: Int) {
        when (menu) {
            R.id.nav_series_catalog -> {
                selectedTitle = R.string.nav_series_catalog
                supportActionUp = NavigationTargets.Catalog.forFragment() as ISupportActionUp
            }
            R.id.nav_series_discover -> {
                selectedTitle = R.string.nav_discover
                supportActionUp = NavigationTargets.Discover.forFragment() as ISupportActionUp
            }
            R.id.nav_show_latest -> {
                selectedTitle = R.string.nav_shows
                supportActionUp = NavigationTargets.Listing.forFragment() as ISupportActionUp
            }
            R.id.nav_show_news -> {
                selectedTitle = R.string.nav_show_news
                supportActionUp = NavigationTargets.News.forFragment() as ISupportActionUp
            }
        }

        bottomAppBar.setTitle(selectedTitle)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        runCatching {
            supportActionUp?.apply {
                this as Fragment
                supportFragmentManager.commit {
                    //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.contentFrame, this@apply, tag)
                }
            }
        }.exceptionOrNull()?.printStackTrace()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [getSupportFragmentManager] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        launch {
            if (selectedItem != 0)
                onNavigate(selectedItem)
            else
                onNavigate(R.id.nav_show_latest)
        }
    }

    companion object {

        private const val keyNavigationSelected = "keyNavigationSelected"
        private const val keyNavigationTitle = "keyNavigationTitle"

        private const val keyShortcutRedirect = "keyShortcutRedirect"
    }
}
