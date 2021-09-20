package org.quranacademy.quran.data

import org.quranacademy.quran.domain.models.Language

interface SurahsListDownloader {

    suspend fun downloadSurahsList(language: Language)

    suspend fun deleteSurahsListForLanguage(language: Language)

}