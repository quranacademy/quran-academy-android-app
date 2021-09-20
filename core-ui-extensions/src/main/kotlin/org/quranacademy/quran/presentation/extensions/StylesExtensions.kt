package org.quranacademy.quran.presentation.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun Context.getThemeColor(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun Context.getThemeDrawable(attr: Int): Drawable {
    val drawableResId = getResIdFromAttribute(attr)
    return resources.getDrawable(drawableResId, theme)
}

fun Context.getResIdFromAttribute(attr: Int): Int {
    if (attr == 0) return 0

    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.resourceId
}