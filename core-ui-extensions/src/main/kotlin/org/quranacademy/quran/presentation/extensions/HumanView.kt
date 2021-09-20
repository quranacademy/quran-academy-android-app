package org.quranacademy.quran.presentation.extensions

import android.os.Build
import android.text.Html
import android.text.Spanned
import java.text.NumberFormat
import java.util.*

private const val SPACE_KB = 1024.0
private const val SPACE_MB = 1024 * SPACE_KB
private const val SPACE_GB = 1024 * SPACE_MB
private const val SPACE_TB = 1024 * SPACE_GB

private val ENGLISH_LOCALE_NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH)

fun Long.bytes2String(isRtl: Boolean = false): String {
    val sizeInBytes = this
    ENGLISH_LOCALE_NUMBER_FORMAT.maximumFractionDigits = 2

    try {
        val size = when {
            //sizeInBytes < SPACE_KB -> nf.format(sizeInBytes) + " Byte(s)"
            sizeInBytes < SPACE_MB -> Pair(ENGLISH_LOCALE_NUMBER_FORMAT.format(sizeInBytes / SPACE_KB), "KB")
            sizeInBytes < SPACE_GB -> Pair(ENGLISH_LOCALE_NUMBER_FORMAT.format(sizeInBytes / SPACE_MB), "MB")
            sizeInBytes < SPACE_TB -> Pair(ENGLISH_LOCALE_NUMBER_FORMAT.format(sizeInBytes / SPACE_GB), "GB")
            else -> Pair(ENGLISH_LOCALE_NUMBER_FORMAT.format(sizeInBytes / SPACE_TB), " TB")
        }
        return if (!isRtl) {
            "${size.first} ${size.second}"
        } else {
            "${size.second} ${size.first}"
        }
    } catch (e: Exception) {
        return "$sizeInBytes Byte(s)"
    }
}

fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}