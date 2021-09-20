package org.quranacademy.quran.presentation.ui.appearance

import kotlinx.coroutines.flow.Flow
import org.quranacademy.quran.domain.models.Language

interface LanguageManager {

    fun setAppLanguage(languageCode: String)

    fun getCurrentAppLanguage(): Language

    fun getLanguageUpdates(): Flow<String>

}