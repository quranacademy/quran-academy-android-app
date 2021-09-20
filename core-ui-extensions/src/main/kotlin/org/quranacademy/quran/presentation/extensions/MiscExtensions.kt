package org.quranacademy.quran.presentation.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager


fun Context.getScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val point = Point()
    display.getRealSize(point)
    return point
}

fun Context.isHorizontalMode(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}