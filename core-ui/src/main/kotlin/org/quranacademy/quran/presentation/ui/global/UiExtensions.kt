package org.quranacademy.quran.presentation.ui.global

import android.content.Context
import android.graphics.Typeface
import android.os.SystemClock
import android.view.View
import com.mta.tehreer.graphics.TypefaceManager
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.domain.models.ArabicFont
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.TranslationFont
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import java.text.NumberFormat
import java.util.*

typealias ArabicTypeface = com.mta.tehreer.graphics.Typeface

fun ArabicFont.getTypeface(context: Context): ArabicTypeface {
    val appearanceManager = getGlobal<AppearanceManager>()
    val fontPathResId = if (this == ArabicFont.UTHMANIC_HAFS) {
        //арабксие тексты с таджвидом не обновлёны для нового шрифта
        //и текст отображается неправильно (танвины),
        //поэтому, для них используем старый шрифт.
        if (appearanceManager.isTajweedEnabled()) {
            R.string.font_uthmanic_hafs_old
        } else {
            R.string.font_uthmanic_hafs
        }
    } else {
        context.resources.getIdentifier(this.resCodeName, "string", context.packageName)
    }
    return TypefaceManager.getTypeface(fontPathResId)!!
}

fun TranslationFont.getTypeface(context: Context): Typeface {
    val fontPathResId = context.resources.getIdentifier(this.resCodeName, "string", context.packageName)
    val fullFontPath = context.getString(fontPathResId)
    return Typeface.createFromAsset(context.assets, fullFontPath)
}

fun getCurrentAppLanguage() = getGlobal<LanguageManager>().getCurrentAppLanguage()

fun Language.isArabic() = this.code == "ar"

private val arabicNumberFormat = NumberFormat.getInstance(Locale("ar", "EG"))

fun Number.toArabicNumberIfNeeded(isArabicLanguage: Boolean = getCurrentAppLanguage().isArabic()): String {
    return if (isArabicLanguage) {
        arabicNumberFormat.format(this)
    } else {
        this.toString()
    }
}

fun View.setDebounceOnClickLister(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}