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

package co.anitrend.support.crunchyroll.core.extensions

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import co.anitrend.arch.extension.preference.delegate.ISupportPreferenceDelegate
import kotlin.reflect.KProperty

private fun ISupportPreferenceDelegate<*>.string(key: Int) = resources.getString(key)

internal class NullableStringPreference(
    override val key: Int,
    override val default: String?,
    override val resources: Resources
) : ISupportPreferenceDelegate<String?> {

    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): String? {
        return thisRef.getString(string(key), default)
    }

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: String?) {
        thisRef.edit {
            putString(string(key), value)
        }
    }
}

internal class FloatPreference(
    override val key: Int,
    override val default: Float,
    override val resources: Resources
) : ISupportPreferenceDelegate<Float> {

    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): Float {
        return thisRef.getFloat(string(key), default)
    }

    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: Float) {
        thisRef.edit {
            putFloat(string(key), value)
        }
    }
}