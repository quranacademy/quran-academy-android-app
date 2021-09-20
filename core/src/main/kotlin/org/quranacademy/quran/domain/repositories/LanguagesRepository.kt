package org.quranacademy.quran.domain.repositories

import kotlinx.coroutines.channels.ReceiveChannel
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.LanguagesWrapper

interface LanguagesRepository {

    suspend fun setAppLanguage(language: Language)

    fun getLanguageChanges(): ReceiveChannel<Language>

    suspend fun getLanguages(forceLoad: Boolean): LanguagesWrapper

    suspend fun downloadLanguageData(language: Language)

    suspend fun removeLanguage(language: Language)

}