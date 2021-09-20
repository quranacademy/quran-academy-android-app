package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getResIdFromAttribute
import org.quranacademy.quran.ui.preferences.io.StorageModule

open class MaterialSeekBarPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialPreference<Int>(context, attrs, defStyleAttr) {

    private var seekBar: AppCompatSeekBar? = null
    private var value: TextView? = null

    private var minValue: Int = 0
    private var maxValue: Int = 0
    private var showValue: Boolean = false

    override fun getLayout(): Int = R.layout.view_seekbar_preference

    override fun getDefaultValue(defValueFromAttrs: String?): Int {
        return defValueFromAttrs?.toInt() ?: 0
    }

    override fun onConfigureSelf() {
        val padding = 16.dp
        setPadding(0, padding, 0, padding)
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
        setBackgroundResource(context.getResIdFromAttribute(android.R.attr.selectableItemBackground))
    }

    override fun onCollectAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialSeekBarPreference)
        try {
            maxValue = ta.getInt(R.styleable.MaterialSeekBarPreference_mp_max_val, 10)
            minValue = ta.getInt(R.styleable.MaterialSeekBarPreference_mp_min_val, 0)
            showValue = ta.getBoolean(R.styleable.MaterialSeekBarPreference_mp_show_val, false)
        } finally {
            ta.recycle()
        }
    }

    override fun onViewCreated() {
        value = findViewById(R.id.mp_value)
        if (showValue) {
            value!!.visibility = View.VISIBLE
        }

        seekBar = findViewById(R.id.mp_seekbar)
        seekBar!!.setOnSeekBarChangeListener(ProgressSaver())
        seekBar!!.max = maxValue - minValue
        setSeekBarValue(getValue())
    }

    override fun getValue(): Int {
        try {
            return getStorageModule().getInt(key, defaultValue!!)
        } catch (e: NumberFormatException) {
            throw AssertionError(context.getString(R.string.exc_not_int_default))
        }

    }

    override fun setValue(value: Int) {
        getStorageModule().saveInt(key, value)
        setSeekBarValue(value)
    }

    override fun setStorageModule(storageModule: StorageModule) {
        super.setStorageModule(storageModule)
        setSeekBarValue(getValue())
    }

    private fun setSeekBarValue(value: Int) {
        seekBar!!.progress = value - minValue
    }

    private inner class ProgressSaver : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            value!!.text = (progress + minValue).toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            setValue(seekBar.progress + minValue)
        }
    }
}
