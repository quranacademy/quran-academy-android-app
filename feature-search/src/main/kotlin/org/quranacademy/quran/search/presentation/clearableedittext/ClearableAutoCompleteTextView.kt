package org.quranacademy.quran.search.presentation.clearableedittext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

import org.quranacademy.quran.search.R

/**
 * Based on https://github.com/Cielsk/clearable-edittext
 */
class ClearableAutoCompleteTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.autoCompleteTextViewStyle
) : AppCompatAutoCompleteTextView(context, attrs, defStyleAttr), TextWatcher {

    private var clearIconDrawable: Drawable? = null
    private var isClearIconShown = false
    private var clearIconDrawWhenFocused = false
    private var textClearedListener: OnTextClearedListener? = null

    init {
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.ClearableAutoCompleteTextView,
                defStyle, 0)

        if (a.hasValue(R.styleable.ClearableAutoCompleteTextView_clearIconDrawable)) {
            clearIconDrawable = a.getDrawable(R.styleable.ClearableAutoCompleteTextView_clearIconDrawable)
            if (clearIconDrawable != null) {
                clearIconDrawable!!.callback = this
            }
        }

        clearIconDrawWhenFocused = a.getBoolean(R.styleable.ClearableEditText_clearIconDrawWhenFocused, true)

        a.recycle()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // no operation
    }

    override fun afterTextChanged(s: Editable) {}

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return if (isClearIconShown) ClearIconSavedState(superState, true) else superState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is ClearIconSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        isClearIconShown = state.isClearIconShown
        showClearIcon(isClearIconShown)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!clearIconDrawWhenFocused || hasFocus()) {
            showClearIcon(!TextUtils.isEmpty(s))
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        showClearIcon(
                (!clearIconDrawWhenFocused || focused) && !TextUtils.isEmpty(text.toString()))
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearIconTouched(event)) {
            setText(null)
            event.action = MotionEvent.ACTION_CANCEL
            showClearIcon(false)
            if (textClearedListener != null)
                textClearedListener!!.onTextCleared()
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun isClearIconTouched(event: MotionEvent): Boolean {
        if (!isClearIconShown) {
            return false
        }

        val touchPointX = event.x.toInt()

        val widthOfView = width
        val compoundPaddingRight = compoundPaddingRight

        return touchPointX >= widthOfView - compoundPaddingRight
    }

    private fun showClearIcon(show: Boolean) {
        if (show) {
            // show icon on the right
            if (clearIconDrawable != null) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, clearIconDrawable, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, DEFAULT_CLEAR_ICON_RES_ID, 0)
            }
        } else {
            // remove icon
            setCompoundDrawables(null, null, null, null)
        }
        isClearIconShown = show
    }

    protected class ClearIconSavedState : BaseSavedState {
        internal val isClearIconShown: Boolean

        private constructor(source: Parcel) : super(source) {
            this.isClearIconShown = source.readByte().toInt() != 0
        }

        internal constructor(superState: Parcelable?, isClearIconShown: Boolean) : super(superState) {
            this.isClearIconShown = isClearIconShown
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByte((if (this.isClearIconShown) 1 else 0).toByte())
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<ClearIconSavedState> = object : Parcelable.Creator<ClearIconSavedState> {
                override fun createFromParcel(source: Parcel): ClearIconSavedState {
                    return ClearIconSavedState(source)
                }

                override fun newArray(size: Int): Array<ClearIconSavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    fun setOnTextClearedListener(textClearedListener: OnTextClearedListener) {
        this.textClearedListener = textClearedListener
    }

    companion object {

        @DrawableRes
        private val DEFAULT_CLEAR_ICON_RES_ID = R.drawable.ic_clear_white_24dp
    }
}