package org.quranacademy.quran.splash.presentation.ui.splash

import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import java.util.*
import javax.inject.Inject

class SystemLanguageManager @Inject constructor() {

    private val baseResources = Philology.getOriginalContext().resources
    private val repository by lazy {
        val languageCode = Locale.getDefault().language
        Philology.getPhilologyRepository(languageCode)
    }

    fun getString(resourceId: Int): String {
        return repository.getString(getResName(resourceId)) ?: baseResources.getString(resourceId)
    }

    private fun getResName(id: Int): String = baseResources.getResourceEntryName(id)

}