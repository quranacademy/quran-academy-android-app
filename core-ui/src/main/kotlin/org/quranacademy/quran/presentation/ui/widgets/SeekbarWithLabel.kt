package org.quranacademy.quran.presentation.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getThemeColor

class SeekbarWithLabel @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val textSizeSeekBar = AppCompatSeekBar(context)
    private val textSizeLabel = TextView(context)

    private var minValue: Int = 0
    private var maxValue: Int = 100

    var onValueChangedListener: ((value: Int) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val verticalPadding = 15.dp
        setPadding(0, verticalPadding, 0, verticalPadding)

        obtainAttrs(attrs)

        val realMaxValue = maxValue - minValue

        textSizeSeekBar.max = realMaxValue
        val textSizeSeekBarLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        textSizeSeekBarLp.weight = 1f
        addView(textSizeSeekBar, textSizeSeekBarLp)

        textSizeLabel.setTextColor(context.getThemeColor(android.R.attr.textColorPrimary))
        textSizeLabel.gravity = Gravity.CENTER
        textSizeLabel.width = 40.dp
        textSizeLabel.text = realMaxValue.toString()
        val textSizeLabelLp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(textSizeLabel, textSizeLabelLp)

        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val realProgress = minValue + progress
                textSizeLabel.text = realProgress.toString()
                onValueChangedListener?.invoke(realProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    fun setValue(value: Int) {
        textSizeLabel.text = value.toString()

        //Temporarily disable the listener and update the value
        val valueChangeListener = onValueChangedListener
        onValueChangedListener = null
        textSizeSeekBar.progress = value - minValue
        onValueChangedListener = valueChangeListener

    }

    private fun obtainAttrs(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SeekbarWithLabel)
        try {
            maxValue = ta.getInt(R.styleable.SeekbarWithLabel_max, 100)
            minValue = ta.getInt(R.styleable.SeekbarWithLabel_min, 0)
        } finally {
            ta.recycle()
        }
    }

}