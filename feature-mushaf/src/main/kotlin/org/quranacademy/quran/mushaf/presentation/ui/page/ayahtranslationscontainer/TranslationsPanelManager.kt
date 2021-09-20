package org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.quranacademy.quran.mushaf.presentation.widgets.TopSheetBehavior
import org.quranacademy.quran.presentation.extensions.dp

class TranslationsPanelManager(
        private val topContainer: ViewGroup,
        private val bottomContainer: ViewGroup,
        private val ayahTranslationsPanel: View
) {

    private val topContainerParams = topContainer.layoutParams as CoordinatorLayout.LayoutParams
    private val bottomContainerParams = bottomContainer.layoutParams as CoordinatorLayout.LayoutParams
    private val topSheetBehavior by lazy { TopSheetBehavior<View>() }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior<View>() }

    fun init() {
        topSheetBehavior.isHideable = true
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = 330.dp
        topContainerParams.behavior = topSheetBehavior
        bottomContainerParams.behavior = bottomSheetBehavior
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun swap() {
        if (isBottom()) {
            bottomContainer.removeView(ayahTranslationsPanel)
            topContainer.addView(ayahTranslationsPanel)
            topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
        } else {
            topContainer.removeView(ayahTranslationsPanel)
            bottomContainer.addView(ayahTranslationsPanel)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    fun show() {
        if (isBottom()) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            topSheetBehavior.state = TopSheetBehavior.STATE_EXPANDED
        }
    }

    fun hide() {
        if (isBottom()) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            topSheetBehavior.state = TopSheetBehavior.STATE_HIDDEN
        }
    }

    fun isBottom() = ayahTranslationsPanel.parent == bottomContainer

    fun getState() = if (isBottom()) {
        TranslationsPanelState.getByBottomSheetState(bottomSheetBehavior.state)
    } else {
        val state = TranslationsPanelState.getByTopSheetState(topSheetBehavior.state)
        if (state == TranslationsPanelState.HALF_OPENED) {
            TranslationsPanelState.CLOSED
        } else state
    }

    fun isShowing(): Boolean {
        return getState() != TranslationsPanelState.CLOSED
    }

    fun onStateChanged(listener: (TranslationsPanelState) -> Unit) {
        addListener(onStateChanged = {
            val state = getState()
            if (state != null) {
                listener(state)
            }
        })
    }

    private fun addListener(
            onStateChanged: ((newState: Int) -> Unit)? = null
    ) {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (isBottom()) onStateChanged?.invoke(newState)
            }
        })
        topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {

            override fun onSlide(topSheet: View, slideOffset: Float, isOpening: Boolean?) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isBottom()) onStateChanged?.invoke(newState)
            }
        })
    }

}