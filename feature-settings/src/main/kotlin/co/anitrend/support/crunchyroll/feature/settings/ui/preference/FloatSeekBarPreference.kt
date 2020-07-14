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

package co.anitrend.support.crunchyroll.feature.settings.ui.preference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import co.anitrend.arch.extension.ext.use
import co.anitrend.support.crunchyroll.core.helper.StorageHelper
import co.anitrend.support.crunchyroll.core.helper.StorageHelper.toHumanReadableByteValue
import co.anitrend.support.crunchyroll.feature.settings.R

internal class FloatSeekBarPreference(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : Preference(context, attrs, defStyleAttr, defStyleRes), SeekBar.OnSeekBarChangeListener {

    var value: Float
        get() = (
                seekBar?.progress
                    ?.times(valueSpacing) ?: 0F
                ) + minValue
        set(v) {
            newValue = v
            persistFloat(v)
            notifyChanged()
        }

    private val moduleTag = FloatSeekBarPreference::class.java.simpleName

    private var minValue: Float = 0f
    private var maxValue: Float = 0f
    private var valueSpacing: Float = 0f
    private var format: String = ""

    private var seekBar: AppCompatSeekBar? = null
    private var seekBarTextView: AppCompatTextView? = null

    private var defaultValue = 0F
    private var newValue = 0F

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, R.attr.seekBarPreferenceStyle)

    constructor(
        context: Context
    ) : this(context, null)

    init {
        widgetLayoutResource = R.layout.preference_float_seek_bar_layout

        context.obtainStyledAttributes(
            attrs, R.styleable.FloatSeekBarPreference,
            defStyleAttr,
            defStyleRes
        ).use {
            minValue = it.getFloat(R.styleable.FloatSeekBarPreference_minValue, 0F)
            maxValue = it.getFloat(R.styleable.FloatSeekBarPreference_maxValue, 1F)
            valueSpacing = it.getFloat(R.styleable.FloatSeekBarPreference_valueSpacing, .1F)
            format = it.getString(R.styleable.FloatSeekBarPreference_format) ?: "%3.1f"
        }
    }

    private fun decoratedFreeStorageSpace(current: Float) {
        val currentFreeSpace = StorageHelper.getFreeSpace(context)
        val usageSpace = (currentFreeSpace * current).toHumanReadableByteValue()
        seekBarTextView!!.text = usageSpace
    }

    // Called when a Preference is being inflated and the default value attribute needs to be read. Since different
    // Preference types have different value types, the subclass should get and return the default value which will be
    // its value type.
    override fun onGetDefaultValue(ta: TypedArray?, i: Int): Any {
        defaultValue = ta!!.getFloat(i, 0F)
        return defaultValue
    }

    // Implement this to set the initial value of the Preference.
    override fun onSetInitialValue(initValue: Any?) {
        newValue = getPersistedFloat(
            if (initValue is Float) initValue
            else this.defaultValue
        )
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder!!.itemView.isClickable = false
        seekBar = holder.findViewById(R.id.seekbar) as AppCompatSeekBar
        seekBarTextView = holder.findViewById(R.id.seekbar_value) as AppCompatTextView

        with (seekBar!!) {
            setOnSeekBarChangeListener(this@FloatSeekBarPreference)
            max = ((maxValue - minValue) / valueSpacing).toInt()
            progress = ((newValue - minValue) / valueSpacing).toInt()
            isEnabled = this@FloatSeekBarPreference.isEnabled
        }
        decoratedFreeStorageSpace(newValue)
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        val v = minValue + progress * valueSpacing
        decoratedFreeStorageSpace(v)
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        val v = minValue + seekbar!!.progress * valueSpacing
        persistFloat(v)
    }
}