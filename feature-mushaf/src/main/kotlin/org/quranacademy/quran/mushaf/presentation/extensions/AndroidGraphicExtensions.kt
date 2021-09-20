package org.quranacademy.quran.mushaf.presentation.extensions

import android.graphics.RectF
import org.quranacademy.quran.domain.models.bounds.AyahBounds

fun AyahBounds.toAndroidBounds(): RectF {
    return RectF(minX.toFloat(), minY.toFloat(), maxX.toFloat(), maxY.toFloat())
}