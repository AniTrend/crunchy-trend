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

package co.anitrend.support.crunchyroll.feature.settings.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.feature.settings.R
import org.koin.android.ext.android.inject

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

    companion object : IFragmentFactory<SettingsFragment> {
        override val FRAGMENT_TAG = SettingsFragment::class.java.simpleName

        override fun newInstance(bundle: Bundle?) = SettingsFragment()
    }
}