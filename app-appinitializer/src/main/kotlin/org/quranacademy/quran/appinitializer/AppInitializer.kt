package org.quranacademy.quran.appinitializer

import android.content.Context
import javax.inject.Inject

class AppInitializer @Inject constructor(
        private val context: Context,
        private val initializers: AppInitializersProvider
) {

    private var isInitialized: Boolean = false

    fun initialize() {
        if (isInitialized) {
            return
        }

        initializers.getInitializers().forEach {
            it.initialize(context)
        }
        isInitialized = true
    }

}