package org.quranacademy.quran.data

import org.quranacademy.quran.domain.models.Language

interface RecitationsListDownloader {

    suspend fun downloadRecitationsList(language: Language)

}