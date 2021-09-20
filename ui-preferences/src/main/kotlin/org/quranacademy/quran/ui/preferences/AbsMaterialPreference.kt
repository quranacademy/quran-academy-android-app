package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import org.quranacademy.quran.presentation.extensions.dp
import org.quranacademy.quran.presentation.extensions.getResIdFromAttribute
import org.quranacademy.quran.ui.preferences.io.MaterialPreferences
import org.quranacademy.quran.ui.preferences.io.StorageModule
import org.quranacademy.quran.ui.preferences.io.UserInputModule
import org.quranacademy.quran.ui.preferences.util.CompositeClickListener

abstract class AbsMaterialPreference<T : Any> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var title: TextView? = null
    private var summary: TextView? = null
    private var icon: ImageView? = null

    protected var key: String
    protected var defaultValue: T? = null

    private var userInputModule: UserInputModule = MaterialPreferences.getUserInputModule(context)
    private var storageModule: StorageModule = MaterialPreferences.getStorageModule(context)

    private var compositeClickListener: CompositeClickListener = CompositeClickListener()

    private lateinit var value: T

    init {
        super.setOnClickListener(compositeClickListener)

        val titleText: String?
        val summaryText: String?
        val iconDrawable: Drawable?
        val iconTintColor: Int

        val ta = context.obtainStyledAttributes(attrs, R.styleable.AbsMaterialPreference)
        try {
            key = ta.getString(R.styleable.AbsMaterialPreference_mp_key) ?: getDefaultKey()

            val defValueFromAttrs = ta.getString(R.styleable.AbsMaterialPreference_mp_default_value)
            defaultValue = getDefaultValue(defValueFromAttrs)

            titleText = ta.getString(R.styleable.AbsMaterialPreference_mp_title)
            summaryText = ta.getString(R.styleable.AbsMaterialPreference_mp_summary)
            iconDrawable = ta.getDrawable(R.styleable.AbsMaterialPreference_mp_icon)
            iconTintColor = ta.getColor(R.styleable.AbsMaterialPreference_mp_icon_tint, -1)
        } finally {
            ta.recycle()
        }

        onCollectAttributes(attrs)
        onConfigureSelf()

        View.inflate(context, getLayout(), this)

        title = findViewById(R.id.mp_title)
        summary = findViewById(R.id.mp_summary)
        icon = findViewById(R.id.mp_icon)

        setTitle(titleText)
        setSummary(summaryText)
        setIcon(iconDrawable)

        if (iconTintColor != -1) {
            setIconColor(iconTintColor)
        }

        onViewCreated()
    }

    protected open fun getDefaultKey(): String {
        throw IllegalStateException("Key attribute for key not found")
    }

    abstract fun getDefaultValue(defValueFromAttrs: String?): T

    @LayoutRes
    protected abstract fun getLayout(): Int

    fun setTitle(@StringRes textRes: Int) {
        setTitle(string(textRes))
    }

    fun setTitle(text: CharSequence?) {
        title!!.visibility = visibility(text)
        title!!.text = text
    }

    fun setSummary(@StringRes textRes: Int) {
        setSummary(string(textRes))
    }

    fun setSummary(text: CharSequence?) {
        summary!!.visibility = visibility(text)
        summary!!.text = text
    }

    fun setIcon(@DrawableRes drawableRes: Int) {
        setIcon(drawable(drawableRes))
    }

    fun setIcon(iconDrawable: Drawable?) {
        icon!!.visibility = if (iconDrawable != null) View.VISIBLE else View.GONE
        icon!!.setImageDrawable(iconDrawable)
    }

    fun setIconColorRes(@ColorRes colorRes: Int) {
        icon!!.setColorFilter(color(colorRes))
    }

    fun setIconColor(@ColorInt color: Int) {
        icon!!.setColorFilter(color)
    }

    fun getTitle(): String {
        return title!!.text.toString()
    }

    fun getSummary(): String {
        return summary!!.text.toString()
    }

    open fun getValue(): T = value

    open fun setValue(value: T) {
        this.value = value
    }

    open fun getUserInputModule() = userInputModule

    open fun setUserInputModule(userInputModule: UserInputModule) {
        this.userInputModule = userInputModule
    }

    open fun getStorageModule() = storageModule

    open fun setStorageModule(storageModule: StorageModule) {
        this.storageModule = storageModule
    }

    /*
     * Returns index of listener. Index may change, so better do not rely heavily on it.
     * It is not a key.
     */
    fun addPreferenceClickListener(listener: View.OnClickListener): Int {
        return compositeClickListener.addListener(listener)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)

        listener?.let {
            compositeClickListener.addListener(it)
        }
    }

    fun removePreferenceClickListener(listener: View.OnClickListener) {
        compositeClickListener.removeListener(listener)
    }

    fun removePreferenceClickListener(index: Int) {
        compositeClickListener.removeListener(index)
    }

    /*
     * Template methods
     */
    protected open fun onCollectAttributes(attrs: AttributeSet) {

    }

    protected open fun onConfigureSelf() {
        setBackgroundResource(context.getResIdFromAttribute(android.R.attr.selectableItemBackground))
        val padding = 16.dp
        setPadding(padding, padding, padding, padding)
        gravity = Gravity.CENTER_VERTICAL
        isClickable = true
    }

    protected open fun onViewCreated() {

    }

    protected fun string(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    protected fun drawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(context, resId)
    }

    protected fun color(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    protected fun visibility(text: CharSequence?): Int {
        return if (!TextUtils.isEmpty(text)) View.VISIBLE else View.GONE
    }

}
