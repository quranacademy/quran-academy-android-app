package org.quranacademy.quran.surahdetails.ui.scroll

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import kotlin.math.abs

class AyahsListSmoothScroller(context: Context) : LinearSmoothScroller(context) {

    override fun getVerticalSnapPreference(): Int = SNAP_TO_START

    override fun calculateTimeForScrolling(dx: Int): Int {
        return 500
    }

    override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
        val viewHeight = abs(viewStart - viewEnd)
        //высота пункта больше высоты экрана (списка): viewHeight > boxHeight
        return if (viewHeight > boxEnd) {
            super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
        } else {
            boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
        }
    }

}