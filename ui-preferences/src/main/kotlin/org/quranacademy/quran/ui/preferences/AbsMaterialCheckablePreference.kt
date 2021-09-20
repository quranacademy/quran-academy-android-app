package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import org.quranacademy.quran.ui.preferences.io.StorageModule

abstract class AbsMaterialCheckablePreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialPreference<Boolean>(context, attrs, defStyleAttr), View.OnClickListener {

    protected lateinit var checkableWidget: Checkable

    override fun onViewCreated() {
        checkableWidget = findViewById<View>(R.id.mp_checkable) as Checkable
        addPreferenceClickListener(this)
        setChecked(getValue())
    }

    override fun onClick(v: View) {
        val newValue = !getValue()
        setChecked(newValue)
        setValue(newValue)
    }

    override fun getValue(): Boolean {
        return getStorageModule().getBoolean(key, defaultValue!!)
    }

    override fun setStorageModule(storageModule: StorageModule) {
        super.setStorageModule(storageModule)
        setChecked(getValue())
    }

    override fun setValue(value: Boolean) {
        getStorageModule().saveBoolean(key, value)
    }

    override fun getDefaultValue(defValueFromAttrs: String?): Boolean {
        return defValueFromAttrs?.toBoolean() ?: false
    }

    private fun setChecked(value: Boolean) {
        (checkableWidget as View).post { checkableWidget.isChecked = value }
    }

}
