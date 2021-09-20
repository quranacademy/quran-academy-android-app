package org.quranacademy.quran.recitationsrepository.recitationaudioinfo

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import org.quranacademy.quran.recitationsrepository.recitations.RecitationsDataSource
import java.io.File
import javax.inject.Inject

class RecitationAudioInfoDataSource @Inject constructor(
        private val recitationsDataSource: RecitationsDataSource,
        private val surahsNameTranslationsDao: SurahsNameTranslationsDao,
        private val audioPathProvider: AudioPathProvider
) {

    suspend fun getRecitationInfo(recitationId: Long): RecitationSurahsAudioInfo {
        val surahsAudioInfo = getRecitationSurahsAudioInfo(recitationId)
        val surahNames = surahsNameTranslationsDao.getAllSurahNames()
        val surahs = surahsAudioInfo.mapIndexed { index, surahAudioInfo ->
            SurahAudioInfo(
                    surahNumber = surahAudioInfo.surahNumber,
                    surahName = surahNames[index].transliteratedName,
                    downloadedAyahsNumber = surahAudioInfo.downloadedAyahsNumber,
                    downloadedAudioSizeBytes = surahAudioInfo.downloadedAudioSizeBytes,
                    isFullyDownloaded = surahAudioInfo.isFullyDownloaded
            )
        }
        return RecitationSurahsAudioInfo(
                recitation = recitationsDataSource.getRecitation(recitationId),
                surahsAudioInfo = surahs
        )
    }

    suspend fun getRecitationsAudioInfo(loadFromInternet: Boolean): List<RecitationAudioInfo> {
        val recitations = recitationsDataSource.getRecitations(loadFromInternet)
        return recitations.recitations.map { recitation ->
            val filesInfo = getRecitationSurahsAudioInfo(recitation.id)
            val downloadedSurahsCount = filesInfo.filter { it.isFullyDownloaded }.size
            val totalRecitationSize = filesInfo.map { it.downloadedAudioSizeBytes }.sum()
            RecitationAudioInfo(
                    recitation = recitation,
                    downloadedSurahsCount = downloadedSurahsCount,
                    downloadedAudioSizeBytes = totalRecitationSize,
                    isFullyDownloaded = downloadedSurahsCount == QuranConstants.SURAHS_COUNT
            )
        }
    }

    private fun getRecitationSurahsAudioInfo(recitationId: Long): List<SurahAudioInfo> {
        val surahsCount = QuranConstants.SURAHS_COUNT
        return (1..surahsCount).map { surahNumber ->
            val surahFolder = audioPathProvider.getRecitationSurahFolder(recitationId, surahNumber)
            val surahFolderSizeBytes = getFolderSizeKb(surahFolder)

            val ayahsNumber = QuranConstants.SURAH_AYAHS_NUMBER[surahNumber - 1]
            val audioFilesNumber = surahFolder.listFiles()?.let { it.size } ?: 0
            val isFullyDownloaded = audioFilesNumber == ayahsNumber
            SurahAudioInfo(
                    surahNumber = surahNumber,
                    surahName = "",
                    downloadedAyahsNumber = audioFilesNumber,
                    downloadedAudioSizeBytes = surahFolderSizeBytes,
                    isFullyDownloaded = isFullyDownloaded
            )
        }
    }

    private fun getFolderSizeKb(directory: File): Long {
        var sizeInBytes: Long = 0
        directory.listFiles()?.let { filesList ->
            for (file in filesList) {
                sizeInBytes += if (file.isFile)
                    file.length()
                else
                    getFolderSizeKb(file)
            }
            return sizeInBytes
        }
        return 0
    }

}