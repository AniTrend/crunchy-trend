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

package co.anitrend.support.crunchyroll.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.commit
import co.anitrend.arch.core.viewmodel.SupportPagingViewModel
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.startNewActivity
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.arch.ui.util.SupportUiKeyStore
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.auth.activity.AuthenticationActivity
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.listings.fragment.FragmentMediaFeedList
import co.anitrend.support.crunchyroll.news.fragment.FragmentFeedNewsList
import co.anitrend.support.crunchyroll.ui.contract.CrunchyActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : CrunchyActivity<Nothing, CrunchyCorePresenter>(), NavigationView.OnNavigationItemSelectedListener {

    private val bottomDrawerBehavior by lazy(LAZY_MODE_UNSAFE) {
        BottomSheetBehavior.from(bottomNavigationDrawer)
    }

    @IdRes
    private var selectedItem: Int = R.id.nav_show_latest

    @StringRes
    private var selectedTitle: Int = R.string.nav_shows

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottomAppBar)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (intent.hasExtra(SupportUiKeyStore.arg_redirect))
            selectedItem = intent.getIntExtra(SupportUiKeyStore.arg_redirect, R.id.nav_show_latest)
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
        bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        floatingShortcutButton.setOnClickListener {
            startNewActivity<AuthenticationActivity>()
            finish()
        }
        bottomNavigationView.apply {
            setNavigationItemSelectedListener(this@MainActivity)
            setCheckedItem(selectedItem)
        }
        onUpdateUserInterface()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SupportUiKeyStore.key_navigation_selected, selectedItem)
        outState.putInt(SupportUiKeyStore.key_navigation_title, selectedTitle)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedItem = savedInstanceState.getInt(SupportUiKeyStore.key_navigation_selected)
        selectedTitle = savedInstanceState.getInt(SupportUiKeyStore.key_navigation_title)
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
                Toast.makeText(this, "onMenuItemClick", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (selectedItem != item.itemId) {
            if (item.groupId != R.id.nav_group_customization) {
                selectedItem = item.itemId
                onNavigate(selectedItem)
            } else
                onNavigate(item.itemId)
        }
        return true
    }

    private fun onNavigate(@IdRes menu: Int) {
        var supportFragment: SupportFragment<*, *, *>? = null
        when (menu) {
            R.id.nav_theme -> toggleTheme()
            R.id.nav_contact -> Toast.makeText(this@MainActivity, "Contact", Toast.LENGTH_SHORT).show()
            R.id.nav_show_latest -> {
                selectedTitle = R.string.nav_shows
                supportFragment = FragmentMediaFeedList.newInstance()
            }
            R.id.nav_show_news -> {
                selectedTitle = R.string.nav_show_news
                supportFragment = FragmentFeedNewsList.newInstance()
            }
        }

        bottomAppBar.setTitle(selectedTitle)
        bottomDrawerBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        supportFragment?.apply {
            supportFragmentActivity = this@apply
            supportFragmentManager.commit {
                replace(R.id.contentFrame, this@apply, tag)
            }
        }
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        if (selectedItem != 0)
            onNavigate(selectedItem)
        else
            onNavigate(R.id.nav_show_latest)
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportPagingViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportPagingViewModel.model]
     *
     * @see [SupportPagingViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
