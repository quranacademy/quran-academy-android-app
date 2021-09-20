package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import org.quranacademy.quran.ui.preferences.io.StorageModule
import org.quranacademy.quran.ui.preferences.io.UserInputModule

abstract class AbsMaterialTextValuePreference<T : Any> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : AbsMaterialPreference<T>(context, attrs, defStyleAttr), UserInputModule.Listener<T>, android.view.View.OnClickListener {

    private var rightValue: TextView? = null
    private var showValueMode: Int = 0

    override fun getLayout(): Int = R.layout.view_text_input_preference

    override fun onCollectAttributes(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AbsMaterialTextValuePreference)
        try {
            showValueMode = ta.getInt(R.styleable.AbsMaterialTextValuePreference_mp_show_value, NOT_SHOW_VALUE)
        } finally {
            ta.recycle()
        }
    }

    override fun onViewCreated() {
        rightValue = findViewById(R.id.mp_right_value)
        showNewValueIfNeeded(toRepresentation(getValue()))
        addPreferenceClickListener(this)
    }

    override fun setStorageModule(storageModule: StorageModule) {
        super.setStorageModule(storageModule)
        showNewValueIfNeeded(toRepresentation(getValue()))
    }

    override fun onInput(value: T) {
        setValue(value)
    }

    protected abstract fun toRepresentation(value: T): CharSequence

    protected fun setRightValue(value: CharSequence) {
        rightValue!!.visibility = visibility(value)
        rightValue!!.text = value
    }

    protected fun showNewValueIfNeeded(value: CharSequence) {
        when (showValueMode) {
            SHOW_ON_RIGHT -> setRightValue(value)
            SHOW_ON_BOTTOM -> setSummary(value)
        }
    }

    protected fun hasSummary(): Boolean {
        return showValueMode != SHOW_ON_BOTTOM && !TextUtils.isEmpty(getSummary())
    }

    companion object {

        private val NOT_SHOW_VALUE = 0
        private val SHOW_ON_RIGHT = 1
        private val SHOW_ON_BOTTOM = 2
    }
}
