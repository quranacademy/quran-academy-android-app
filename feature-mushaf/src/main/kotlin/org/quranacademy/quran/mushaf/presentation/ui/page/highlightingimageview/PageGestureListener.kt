package org.quranacademy.quran.mushaf.presentation.ui.page.highlightingimageview

import android.graphics.Matrix
import android.view.GestureDetector
import android.view.MotionEvent
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.PageTouchEvent

abstract class PageGestureListener(
        private val highlightingImageView: HighlightingImageView
) : GestureDetector.SimpleOnGestureListener() {

    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        handleTouchEvent(event, PageTouchEvent.TouchType.SINGLE)
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        handleTouchEvent(event, PageTouchEvent.TouchType.DOUBLE)
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        handleTouchEvent(event, PageTouchEvent.TouchType.LONG)
    }

    private fun handleTouchEvent(event: MotionEvent, type: PageTouchEvent.TouchType) {
        if (highlightingImageView.drawable == null) {
            return
        }

        val inverse = Matrix()
        if (highlightingImageView.imageMatrix.invert(inverse)) {
            val originalX = event.x
            val originalY = event.y - highlightingImageView.paddingTop
            val results = FloatArray(2)
            inverse.mapPoints(results, floatArrayOf(originalX, originalY))
            onTouchEvent(PageTouchEvent(results[0].toInt(), results[1].toInt(), type))
        }
    }

    abstract fun onTouchEvent(event: PageTouchEvent)

}