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

package co.anitrend.support.crunchyroll.feature.settings.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.anitrend.support.crunchyroll.feature.settings.R

class LocaleContentScreen : PreferenceFragmentCompat() {

    /**
     * Called during [.onCreate] to supply the preferences for this fragment.
     * Subclasses are expected to call [.setPreferenceScreen] either
     * directly or via helper methods such as [.addPreferencesFromResource].
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     * [PreferenceScreen] with this key.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_locale, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findPreference<ListPreference>(
            getString(R.string.settings_configuration_locale)
        )?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                runCatching {
                    activity?.recreate()
                }.exceptionOrNull()?.printStackTrace()
                true
            }
    }
}