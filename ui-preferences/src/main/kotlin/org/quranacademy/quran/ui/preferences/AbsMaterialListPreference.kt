package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet

internal abstract class AbsMaterialListPreference<T : Any> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialTextValuePreference<T>(context, attrs, defStyleAttr) {

    protected var entries: Array<CharSequence>? = null
    protected var entryValues: Array<CharSequence>? = null

    override fun onCollectAttributes(attrs: AttributeSet) {
        super.onCollectAttributes(attrs)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.AbsMaterialListPreference)
        try {
            if (ta.hasValue(R.styleable.AbsMaterialListPreference_mp_entry_descriptions)) {
                entries = ta.getTextArray(R.styleable.AbsMaterialListPreference_mp_entry_descriptions)
            }

            if (ta.hasValue(R.styleable.AbsMaterialListPreference_mp_entry_values)) {
                entryValues = ta.getTextArray(R.styleable.AbsMaterialListPreference_mp_entry_values)
            }
        } finally {
            ta.recycle()
        }

        if (entries == null || entryValues == null) {
            if (entries != null) {
                entryValues = entries
            } else if (entryValues != null) {
                entries = entryValues
            } else {
                throw AssertionError(context.getString(R.string.exc_no_entries_to_list_provided))
            }
        }
    }

}
