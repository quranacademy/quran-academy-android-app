package org.quranacademy.quran.data.translations

import org.quranacademy.quran.domain.models.Language

interface TranslationsListDownloader {

    suspend fun updateTranslationsList(language: Language? = null)

}