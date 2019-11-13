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
import android.view.View
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.feature.settings.R
import co.anitrend.support.crunchyroll.feature.settings.koin.injectFeatureModules
import kotlinx.android.synthetic.main.settings_activity.*
import org.koin.android.ext.android.inject

class SettingsScreen : CrunchyActivity<Nothing, CrunchyCorePresenter>() {

    private lateinit var systemChromeFader: ElasticDragDismissFrameLayout.SystemChromeFader

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

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
        injectFeatureModules()
        onUpdateUserInterface()
        floatingShortcutButton.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "shows theme bottom sheet tools",
                Toast.LENGTH_SHORT
            ).show()
        }
        systemChromeFader =
            object : ElasticDragDismissFrameLayout.SystemChromeFader(
                this
            ) {
                override fun onDragDismissed() {
                    closeScreen()
                }
            }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        draggableFrame.addListener(systemChromeFader)
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        draggableFrame.removeListener(systemChromeFader)
        super.onPause()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        supportFragmentManager.commit {
            replace(R.id.settings_content, SettingsFragment())
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val settings by inject<IAuthenticationSettings>()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            findPreference<Preference>(
                getString(R.string.preference_key_accounts)
            )?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    when (settings.isAuthenticated) {
                        true -> {
                            NavigationTargets.Authentication(activity)
                            activity?.closeScreen()
                            true
                        }
                        else -> {
                            Toast.makeText(
                                context,
                                "You are not currently logged in",
                                Toast.LENGTH_SHORT
                            ).show()
                            false
                        }
                    }
            }
        }
    }
}