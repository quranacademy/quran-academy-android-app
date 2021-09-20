package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet

class MaterialSwitchPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialCheckablePreference(context, attrs, defStyleAttr) {

    override fun getLayout(): Int = R.layout.view_switch_preference

}
