package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.TextView
import org.quranacademy.quran.presentation.extensions.getThemeColor
import org.quranacademy.quran.presentation.extensions.visible

class MaterialPreferenceWithLabel @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialPreference<String>(context, attrs, defStyleAttr) {

    private val label: TextView by lazy { findViewById<TextView>(R.id.mp_label) }

    init {
        label.background.setColorFilter(context.getThemeColor(R.attr.colorAccent), PorterDuff.Mode.MULTIPLY)
    }

    override fun getDefaultValue(defValueFromAttrs: String?): String = ""

    override fun getValue(): String {
        return label.text.toString()
    }

    override fun setValue(value: String) {
        this.label.text = value
        label.visible(true)
    }

    override fun getDefaultKey() = ""

    override fun getLayout(): Int = R.layout.item_pref_with_label

    fun hideLabel() = label.visible(false)

}
