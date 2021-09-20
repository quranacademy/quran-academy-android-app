package org.quranacademy.quran.mainscreen.presentation.ui.jumptoayah

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

/**
 * AutoCompleteTextView that forces to use value from one of the values in adapter (choices).
 */
class ForceCompleteTextView : AppCompatAutoCompleteTextView {

    /* Thanks to those in http://stackoverflow.com/q/15544943/1197317 for inspiration */
    var onForceCompleteListener: ((v: ForceCompleteTextView, position: Int, rowId: Long) -> Unit)? = null
        set(value) {
            field = value
            post {
                if (!isFocused) {
                    onForceComplete(AdapterView.INVALID_POSITION, AdapterView.INVALID_ROW_ID)
                }
            }
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setOnItemClickListener { parent, view, position, id -> onForceComplete(position, id) }
    }

    protected fun onForceComplete(position: Int, rowId: Long) {
        onForceCompleteListener?.invoke(this, position, rowId)
    }

    override fun enoughToFilter(): Boolean {
        // Break the limit of minimum 1
        return true
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            if (filter != null) {
                performFiltering(text, 0)
            }
        } else {
            onForceComplete(AdapterView.INVALID_POSITION, AdapterView.INVALID_ROW_ID)
        }
    }

    /**
     * Do not call this method, use [.setOnForceCompleteListener]
     * instead.
     *
     * @throws UnsupportedOperationException if called
     */
    override fun setOnItemClickListener(l: AdapterView.OnItemClickListener) {
        throw UnsupportedOperationException("Call setOnForceCompleteListener instead")
    }

}