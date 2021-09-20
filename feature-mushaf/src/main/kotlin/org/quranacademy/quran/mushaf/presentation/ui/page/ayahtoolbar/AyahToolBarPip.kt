package org.quranacademy.quran.mushaf.presentation.ui.page.ayahtoolbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.presentation.ui.page.ayahtoolbar.AyahToolBar.PipPosition

class AyahToolBarPip @kotlin.jvm.JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var path: Path? = null
    private var paint: Paint = Paint()
    private var position: PipPosition = PipPosition.DOWN

    init {
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.ayah_toolbar_background)
        paint.style = Paint.Style.FILL_AND_STROKE
    }

    fun setPosition(position: PipPosition) {
        this.position = position
        updatePoints()
    }

    private fun updatePoints() {
        val width = width
        val height = height
        val pointA: Point
        val pointB: Point
        val pointC: Point
        if (position === PipPosition.DOWN) {
            pointA = Point(width / 2, height)
            pointB = Point(0, 0)
            pointC = Point(width, 0)
        } else {
            pointA = Point(width / 2, 0)
            pointB = Point(0, height)
            pointC = Point(width, height)
        }

        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(pointA.x.toFloat(), pointA.y.toFloat())
        path.lineTo(pointB.x.toFloat(), pointB.y.toFloat())
        path.lineTo(pointC.x.toFloat(), pointC.y.toFloat())
        path.close()
        this.path = path

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updatePoints()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path!!, paint)
    }

}