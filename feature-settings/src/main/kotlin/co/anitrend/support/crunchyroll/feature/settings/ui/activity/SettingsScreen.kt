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

package co.anitrend.support.crunchyroll.feature.settings.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.feature.settings.R
import co.anitrend.support.crunchyroll.feature.settings.ui.fragment.SettingsFragment
import kotlinx.android.synthetic.main.settings_activity.*
import co.anitrend.support.crunchyroll.core.ui.commit

class SettingsScreen : CrunchyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(bottomAppBar)
    }

    /**
     * Additional initialization to be done in this method, if the overriding class is type of
     * [androidx.fragment.app.Fragment] then this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate]. Otherwise
     * [androidx.fragment.app.FragmentActivity.onPostCreate] invokes this function
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        onUpdateUserInterface()
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to [menu].
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [onPrepareOptionsMenu].
     *
     * The default implementation populates the menu with standard system
     * menu items. These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time [onCreateOptionsMenu] is called.
     *
     * When you add items to the menu, you can implement the Activity's
     * [onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see onPrepareOptionsMenu
     * @see onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                Toast.makeText(
                    applicationContext,
                    "shows theme bottom sheet tools",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onUpdateUserInterface() {
        currentFragmentTag = FragmentItem(
            fragment = SettingsFragment::class.java
        ).commit(R.id.settings_content, this) {}
    }
}