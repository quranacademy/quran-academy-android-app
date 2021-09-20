package org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer

import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.quranacademy.quran.mushaf.presentation.widgets.TopSheetBehavior

enum class TranslationsPanelState(
        val topSheetStateCode: Int,
        val bottomSheetStateCode: Int
) {

    OPENED(TopSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_EXPANDED),
    HALF_OPENED(TopSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_COLLAPSED),
    CLOSED(TopSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_HIDDEN);

    companion object {
        fun getByTopSheetState(stateCode: Int) = values().firstOrNull {
            it.topSheetStateCode == stateCode
        }

        fun getByBottomSheetState(stateCode: Int) = values().firstOrNull {
            it.bottomSheetStateCode == stateCode
        }
    }

}