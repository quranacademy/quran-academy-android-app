package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet

class MaterialStandardPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialPreference<Unit>(context, attrs, defStyleAttr) {

    override fun getLayout(): Int = R.layout.view_standard_preference

    override fun getValue(): Unit = Unit

    override fun setValue(value: Unit) {}

    override fun getDefaultKey(): String = ""

    override fun getDefaultValue(defValueFromAttrs: String?): Unit = Unit

}
