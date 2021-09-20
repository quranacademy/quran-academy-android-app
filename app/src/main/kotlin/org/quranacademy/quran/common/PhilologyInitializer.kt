package org.quranacademy.quran.common

import android.content.Context
import dev.b3nedikt.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.NewCalligraphyInterceptor
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.presentation.languagesystem.HQAViewTransformerFactory
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import org.quranacademy.quran.presentation.ui.languagesystem.PhilologyInterceptor
import javax.inject.Inject

class PhilologyInitializer @Inject constructor(
        private val appPreferences: AppPreferences
) : AppInitializerElement {

    override fun initialize(context: Context) {
        Philology.init(appPreferences.getAppLanguage(), HQAViewTransformerFactory)

        val calligraphyInterceptor = NewCalligraphyInterceptor(CalligraphyConfig.Builder().build())
        ViewPump.init(
                PhilologyInterceptor,
                calligraphyInterceptor
        )
    }

}