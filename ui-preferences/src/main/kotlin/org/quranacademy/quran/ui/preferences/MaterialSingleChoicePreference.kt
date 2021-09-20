package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import org.quranacademy.quran.ui.preferences.io.StorageModule

class MaterialSingleChoicePreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialTextValuePreference<String>(context, attrs, defStyleAttr) {

    private lateinit var entryTitles: Array<String>
    protected lateinit var entryValues: Array<String>

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AbsMaterialListPreference)
        try {
            if (ta.hasValue(R.styleable.AbsMaterialListPreference_mp_entry_descriptions)) {
                entryTitles = ta.getTextArray(R.styleable.AbsMaterialListPreference_mp_entry_descriptions).map { it as String }.toTypedArray()
            }

            if (ta.hasValue(R.styleable.AbsMaterialListPreference_mp_entry_values)) {
                entryValues = ta.getTextArray(R.styleable.AbsMaterialListPreference_mp_entry_values).map { it as String }.toTypedArray()
            }
        } finally {
            ta.recycle()
        }
    }

    fun setData(entryTitles: Array<String>, entryValues: Array<String>, currentItemValue: String) {
        this.entryTitles = entryTitles
        this.entryValues = entryValues
        setValue(currentItemValue)
    }

    override fun getDefaultValue(defValueFromAttrs: String?): String = ""

    override fun getValue(): String {
        return getStorageModule().getString(key, "")
    }

    override fun setValue(value: String) {
        getStorageModule().saveString(key, value)
        showNewValueIfNeeded(toRepresentation(value))
    }

    override fun setStorageModule(storageModule: StorageModule) {
        super.setStorageModule(storageModule)
        showNewValueIfNeeded(toRepresentation(getValue()))
    }

    override fun onClick(v: View) {
        if (isDataNotInitialized()) {
            throw AssertionError(context.getString(R.string.exc_no_entries_to_list_provided))
        }

        getUserInputModule()
                .showSingleChoiceInput(key, getTitle(), entryTitles, entryValues, getItemPosition(getValue()), this)
    }

    override fun toRepresentation(value: String): CharSequence {
        if (isDataNotInitialized()) {
            return ""
        }

        for (i in entryValues.indices) {
            if (entryValues[i] == value) {
                return entryTitles[i]
            }
        }
        return ""
    }

    private fun getItemPosition(value: String): Int {
        for (i in entryValues.indices) {
            if (entryValues[i] == value) {
                return i
            }
        }
        return -1
    }

    private fun isDataNotInitialized(): Boolean = !::entryTitles.isInitialized || !::entryValues.isInitialized

}
