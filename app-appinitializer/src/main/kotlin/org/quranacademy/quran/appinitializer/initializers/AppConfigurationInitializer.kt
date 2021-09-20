package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import org.quranacademy.quran.appinitializer.AppInitializerElement
import javax.inject.Inject

class AppConfigurationInitializer @Inject constructor() : AppInitializerElement {

    override fun initialize(context: Context) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}