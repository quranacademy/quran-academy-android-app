package org.quranacademy.quran.data.translations

import org.quranacademy.quran.domain.models.Language

interface WordByWordTranslationsListDownloader {

    suspend fun updateTranslationsList(language: Language? = null)

}