package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import org.quranacademy.quran.presentation.extensions.getThemeColor

class MaterialPreferenceCategory @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var container: ViewGroup
    private var isContainerPrepared = false
    private var title: TextView

    init {
        val titleColor: Int
        val titleText: String
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MaterialPreferenceCategory)
        try {
            titleText = ta.getString(R.styleable.MaterialPreferenceCategory_mpc_title) ?: ""
            titleColor = ta.getColor(R.styleable.MaterialPreferenceCategory_mpc_title_color, -1)
        } finally {
            ta.recycle()
        }

        View.inflate(context, R.layout.view_preference_category, this)

        setBackgroundColor(context.getThemeColor(R.attr.windowBackground))
        elevation = 7f
        useCompatPadding = true
        radius = 0f
        container = findViewById<View>(R.id.mpc_container) as ViewGroup
        title = findViewById<View>(R.id.mpc_title) as TextView

        if (titleText.isNotEmpty()) {
            title.visibility = View.VISIBLE
            title.text = titleText
        }

        if (titleColor != -1) {
            title.setTextColor(titleColor)
        }

        isContainerPrepared = true
    }

    fun setTitle(@StringRes textRes: Int) {
        setTitle(context.getString(textRes))
    }

    fun setTitle(titleText: String) {
        title.visibility = View.VISIBLE
        title.text = titleText
    }

    fun setTitleColor(@ColorInt color: Int) {
        title.setTextColor(color)
    }

    fun setTitleColorRes(@ColorRes colorRes: Int) {
        title.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    override fun addView(child: View) {
        if (isContainerPrepared) {
            container.addView(child)
        } else {
            super.addView(child)
        }
    }

    override fun addView(child: View, index: Int) {
        if (isContainerPrepared) {
            container.addView(child, index)
        } else {
            super.addView(child, index)
        }
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isContainerPrepared) {
            container.addView(child, params)
        } else {
            super.addView(child, params)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isContainerPrepared) {
            container.addView(child, index, params)
        } else {
            super.addView(child, index, params)
        }
    }

}
