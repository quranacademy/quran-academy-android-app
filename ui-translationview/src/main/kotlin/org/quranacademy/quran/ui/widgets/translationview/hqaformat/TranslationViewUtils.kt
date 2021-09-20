package org.quranacademy.quran.ui.widgets.translationview.hqaformat

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.getVectorIconBitmap(resId: Int): Bitmap? {
    val drawable = this.resources.getDrawable(resId)
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
        getBitmapFromVectorDrawable(drawable)
    } else {
        null
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
private fun getBitmapFromVectorDrawable(drawable: Drawable): Bitmap {
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}