package org.quranacademy.quran.mushaf.presentation.ui.page

import android.content.Context
import android.graphics.RectF
import android.view.ViewGroup
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.presentation.extensions.toAndroidBounds
import org.quranacademy.quran.mushaf.presentation.ui.page.ayahtoolbar.AyahToolBar
import org.quranacademy.quran.mushaf.presentation.ui.page.highlightingimageview.HighlightingImageView

class ToolbarPositionCalculator(
        private val context: Context,
        private val ayahToolBar: AyahToolBar,
        private val pageContainer: ViewGroup,
        private val mushafPageImage: HighlightingImageView
) {

    fun getToolBarPosition(
            bounds: List<AyahBounds>,
            isPanelShowing: Boolean,
            isPanelAtBottom: Boolean
    ): AyahToolBar.AyahToolBarPosition {
        val (y, pipPosition, chosenRect) = calculateY(bounds, isPanelShowing, isPanelAtBottom)
        val (midpoint, x) = calculateX(chosenRect)
        return AyahToolBar.AyahToolBarPosition(
                x = x,
                y = y,
                pipOffset = midpoint - x,
                pipPosition = pipPosition
        )
    }

    private fun calculateX(chosenRect: RectF): Pair<Float, Float> {
        val toolBarWidth = ayahToolBar.getToolBarWidth()
        val screenWidth = pageContainer.width

        val midpoint = chosenRect.centerX()
        var x = midpoint - toolBarWidth / 2
        if (x < 0 || x + toolBarWidth > screenWidth) {
            x = chosenRect.left
            if (x + toolBarWidth > screenWidth) {
                x = (screenWidth - toolBarWidth).toFloat()
            }
        }
        return Pair(midpoint, x)
    }

    private fun calculateY(
            bounds: List<AyahBounds>,
            isPanelShowing: Boolean,
            isPanelAtBottom: Boolean
    ): Triple<Float, AyahToolBar.PipPosition, RectF> {
        val toolBarHeight = context.resources.getDimensionPixelSize(R.dimen.ayah_toolbar_total_height)
        val screenHeight = pageContainer.height
        val matrix = mushafPageImage.imageMatrix

        var pipPosition = AyahToolBar.PipPosition.DOWN

        val firstRect = RectF()
        matrix.mapRect(firstRect, bounds.first().toAndroidBounds())
        val chosenRect = RectF(firstRect)

        var y = firstRect.top - toolBarHeight
        val isToolbarTooCloseToTop = (y - toolBarHeight) < 0
        val isToolbarTooCloseToPanel = if (isPanelShowing && isPanelAtBottom) {
            val screenMiddle = screenHeight / 2
            (y - toolBarHeight) < screenMiddle
        } else false

        if (isToolbarTooCloseToTop || isToolbarTooCloseToPanel) {
            pipPosition = AyahToolBar.PipPosition.UP
            // too close to the top, let's move to the bottom
            matrix.mapRect(chosenRect, bounds.last().toAndroidBounds())
            y = chosenRect.bottom
            //val tooCloseToBottom = y > screenHeight - toolBarHeight
            //if (tooCloseToBottom) {
            //    //показываем снизу первой линии аята
            //    y = firstRect.bottom
            //    chosenRect = firstRect
            //}
        }

        return Triple(y, pipPosition, chosenRect)
    }

}