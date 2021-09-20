package org.quranacademy.quran.presentation.ui.widgets.waterfalltoolbar

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.presentation.extensions.dp

open class WaterfallToolbar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.editTextStyle
) : CardView(context, attrs, defStyleAttr) {

    fun updateScroll(dy: Int) {
        // real position must always get updated
        realPosition += dy
        mutualScrollListenerAction()
    }

    /**
     * The three variables ahead are null safe, since they are always set
     * at least once in init() and a null value can't be assigned to them
     * after that. So all the "!!" involving them below are fully harmless.
     */

    /**
     * The elevation with which the toolbar starts
     */
    var initialElevation: Int? = null
        set(value) {
            if (value != null) {
                field = value

                // got to update elevation in case this value have
                // been set in a running and visible activity
                if (isSetup) adjustCardElevation()

            } else throw NullPointerException("This field cannot be null.")
        }

    /**
     * The elevation the toolbar gets when it reaches final scroll elevation
     */
    var finalElevation: Int? = null
        set(value) {
            if (value != null) {
                field = value

                // got to update elevation in case this value have
                // been set in a running and visible activity
                if (isSetup) adjustCardElevation()

            } else throw NullPointerException("This field cannot be null.")
        }

    /**
     * The percentage of the screen's height that is
     * going to be scrolled to reach the final elevation
     */
    var scrollFinalPosition: Int? = null
        set(value) {
            if (value != null) {
                val screenHeight = resources.displayMetrics.heightPixels
                field = Math.round(screenHeight * (value / 100.0f))

                // got to update elevation in case this value have
                // been set in a running and visible activity
                if (isSetup) adjustCardElevation()

            } else throw NullPointerException("This field cannot be null.")
        }

    /**
     * Dimension units (dp and pixel) auxiliary
     */


    /**
     * Values related to Waterfall Toolbar behavior in their default forms
     */
    val defaultInitialElevation: Int = 0
    val defaultFinalElevation: Int = 2.dp
    val defaultScrollFinalElevation = 12

    /**
     * Auxiliary that indicates if the view is already setup
     */
    private var isSetup: Boolean = false

    /**
     * Position in which toolbar must be to reach expected shadow
     */
    private var orthodoxPosition: Int = 0

    /**
     * Recycler/scroll view real position
     */
    private var realPosition: Int = 0

    init {
        // leave card corners square
        radius = 0f

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterfallToolbar)

            val rawInitialElevation = typedArray.getDimensionPixelSize(
                    R.styleable.WaterfallToolbar_initial_elevation, defaultInitialElevation)

            val rawFinalElevation = typedArray.getDimensionPixelSize(
                    R.styleable.WaterfallToolbar_final_elevation, defaultFinalElevation)

            scrollFinalPosition = typedArray.getInteger(
                    R.styleable.WaterfallToolbar_scroll_final_elevation, defaultScrollFinalElevation)

            this.initialElevation = rawInitialElevation.dp
            this.finalElevation = rawFinalElevation.dp

            typedArray.recycle()

        } else {

            initialElevation = defaultInitialElevation
            finalElevation = defaultFinalElevation
            scrollFinalPosition = defaultScrollFinalElevation
        }

        adjustCardElevation()    // just to make sure card elevation is set

        isSetup = true
    }

    /**
     * These lines are common in both scroll listeners, so they are better joined
     */
    private fun mutualScrollListenerAction() {
        // orthodoxPosition can't be higher than scrollFinalPosition because
        // the last one holds the position in which shadow reaches ideal size

        if (realPosition <= scrollFinalPosition!!) {
            orthodoxPosition = realPosition
        } else {
            orthodoxPosition = scrollFinalPosition!!
        }

        adjustCardElevation()
    }

    /**
     * Speed up the card elevation setting
     */
    private fun adjustCardElevation() {
        cardElevation = calculateElevation().toFloat()
    }

    /**
     * Calculates the elevation based on given attributes and scroll
     * @return New calculated elevation
     */
    private fun calculateElevation(): Int {
        // getting back to rule of three:
        // finalElevation = scrollFinalPosition
        // newElevation   = orthodoxPosition
        var newElevation: Int = finalElevation!! * orthodoxPosition / scrollFinalPosition!!

        // avoid values under minimum value
        if (newElevation < initialElevation!!) newElevation = initialElevation!!

        return newElevation.dp
    }

    /**
     * Saves the view's current dynamic state in a parcelable object
     * @return A parcelable with the saved data
     */
    override fun onSaveInstanceState(): Parcelable {
        val savedState = SavedState(super.onSaveInstanceState())

        savedState.elevation = cardElevation.toInt()
        savedState.orthodoxPosition = orthodoxPosition
        savedState.realPosition = realPosition

        return savedState
    }

    /**
     * Restore the view's dynamic state
     * @param state The frozen state that had previously been returned by onSaveInstanceState()
     */
    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)

            // setting card elevation doesn't work until view is created
            post {
                // it's safe to use "!!" here, since savedState will
                // always store values properly set in onSaveInstanceState()
                cardElevation = state.elevation!!.toFloat()
                orthodoxPosition = state.orthodoxPosition!!
                realPosition = state.realPosition!!
            }

        } else {

            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Custom parcelable to store this view's dynamic state
     */
    private class SavedState : BaseSavedState {
        var elevation: Int? = null
        var orthodoxPosition: Int? = null
        var realPosition: Int? = null

        internal constructor(source: Parcel) : super(source)

        @RequiresApi(api = Build.VERSION_CODES.N)
        internal constructor(source: Parcel, loader: ClassLoader) : super(source, loader)

        internal constructor(superState: Parcelable?) : super(superState)

        companion object {
            @JvmField
            internal val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}