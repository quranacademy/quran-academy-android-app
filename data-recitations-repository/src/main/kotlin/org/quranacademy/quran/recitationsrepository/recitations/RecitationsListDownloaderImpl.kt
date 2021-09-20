package org.quranacademy.quran.recitationsrepository.recitations

import org.quranacademy.quran.data.RecitationsListDownloader
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class RecitationsListDownloaderImpl @Inject constructor(
        private val recitationsDataSource: RecitationsDataSource
) : RecitationsListDownloader {

    override suspend fun downloadRecitationsList(language: Language) {
        recitationsDataSource.downloadRecitationsFromServer(language.code)
    }

}