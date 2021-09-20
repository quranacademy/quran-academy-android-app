package org.quranacademy.quran.mushaf.presentation.ui.page.highlightingimageview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.PageBounds
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.presentation.extensions.toAndroidBounds
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.PageTouchEvent
import org.quranacademy.quran.presentation.extensions.getThemeColor
import java.util.*
import kotlin.math.roundToInt

class HighlightingImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    var pageBounds: PageBounds? = null
    var onPageTouchListener: ((event: PageTouchEvent) -> Unit)? = null

    private var ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>> = TreeMap()

    // cached objects for onDraw
    private val scaledRect = RectF()
    private val alreadyHighlighted = mutableSetOf<AyahId>()

    init {
        val gestureDetector = GestureDetector(context, object : PageGestureListener(this) {
            override fun onTouchEvent(event: PageTouchEvent) {
                onPageTouchListener?.invoke(event)
            }
        })

        setOnTouchListener { _: View, event: MotionEvent ->
            gestureDetector.onTouchEvent(event)
        }
    }

    fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>) {
        this.ayahHighlights.clear()
        this.ayahHighlights.putAll(ayahHighlights)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (drawable == null) {
            // no image, forget it.
            return
        }

        val matrix = imageMatrix
        // Draw each ayah highlight
        val pageAyahsCoordinates = this.pageBounds?.pageAyahsBounds
        if (pageAyahsCoordinates != null && !ayahHighlights.isEmpty()) {
            alreadyHighlighted.clear()
            for (entry in ayahHighlights.entries) {
                val highlightType = entry.key
                val highlightedAyahs = entry.value
                drawAyahHighlightsForType(highlightType, highlightedAyahs, pageAyahsCoordinates, matrix, canvas)
            }
        }
    }

    private fun drawAyahHighlightsForType(
            highlightType: AyahHighlightType,
            highlightedAyahs: Set<AyahId>,
            pageAyahsCoordinates: Map<AyahId, List<AyahBounds>>,
            matrix: Matrix,
            canvas: Canvas
    ) {
        val paint = getPaintForHighlightType(highlightType)
        for (ayah in highlightedAyahs) {
            //an ayah can be highlighted by different highlight types
            if (alreadyHighlighted.contains(ayah)) continue
            val ayahCoordinates = pageAyahsCoordinates[ayah]
            if (ayahCoordinates != null && !ayahCoordinates.isEmpty()) {
                drawAyahHighlighting(ayahCoordinates, matrix, canvas, paint, ayah)
            }
        }
    }

    private fun drawAyahHighlighting(
            coordinates: List<AyahBounds>,
            matrix: Matrix,
            canvas: Canvas,
            paint: Paint,
            ayahId: AyahId
    ) {
        for (ayahCoordinates in coordinates) {
            val bounds = ayahCoordinates.toAndroidBounds()
            matrix.mapRect(scaledRect, bounds)
            scaledRect.offset(0f, paddingTop.toFloat())
            canvas.drawRect(scaledRect, paint)
        }
        alreadyHighlighted.add(ayahId)
    }

    private fun getPaintForHighlightType(type: AyahHighlightType): Paint {
        var paint = SPARSE_PAINT_ARRAY.get(type.ordinal)
        if (paint == null) {
            paint = Paint()
            val color = getColorForHighlightType(type)
            paint.color = color
            SPARSE_PAINT_ARRAY.put(color, paint)
        }
        return paint
    }

    private fun getColorForHighlightType(type: AyahHighlightType): Int {
        return when (type) {
            AyahHighlightType.SELECTION -> context.getThemeColor(R.attr.mushafSelectionHighlightColor)
            AyahHighlightType.BOOKMARK -> context.getThemeColor(R.attr.mushafBookmarkHighlightColor)
            AyahHighlightType.AUDIO -> context.getThemeColor(R.attr.mushafAudioHighlightColor)
            AyahHighlightType.HIDDEN -> adjustAlpha(context.getThemeColor(R.attr.mushafPageBackgroundColor), 0.9f)
        }
    }

    @ColorInt
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    companion object {
        private val SPARSE_PAINT_ARRAY = SparseArray<Paint>()
    }

}