package org.quranacademy.quran.presentation.ui.languagesystem

import android.content.Context
import android.content.res.Configuration
import java.util.*

class ContextLocaleConfigurator {

    companion object {

        fun configContext(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val res = context.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        }

    }

}