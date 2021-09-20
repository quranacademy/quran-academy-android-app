package org.quranacademy.quran.ayahdetails.domain

import org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository.AyahBookmarksRepository
import org.quranacademy.quran.bookmarks.data.folders.BookmarkFoldersRepository
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.sharingdialog.sharingmanager.AyahsTextSharingManager
import org.quranacademy.quran.domain.models.*
import org.quranacademy.quran.domain.repositories.AyahsRepository
import org.quranacademy.quran.domain.repositories.TranslationsRepository
import org.quranacademy.quran.player.domain.PlayerControl
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import javax.inject.Inject

class AyahDetailsInteractor @Inject constructor(
        private val ayahsRepository: AyahsRepository,
        private val ayahBookmarksRepository: AyahBookmarksRepository,
        private val bookmarkFoldersRepository: BookmarkFoldersRepository,
        private val translationsRepository: TranslationsRepository,
        private val recitationsRepository: RecitationsRepository,
        private val playerControl: PlayerControl
) {

    suspend fun getAyahDetails(ayahId: AyahId): AyahDetails = ayahsRepository.getAyahDetails(ayahId, loadAllTranslations = false)

    suspend fun playAyah(ayahId: AyahId) {
        val recitations = recitationsRepository.getRecitations()
        val range = VerseRange(
                startSurah = ayahId.surahNumber,
                startAyah = ayahId.ayahNumber,
                endingSurah = ayahId.surahNumber,
                endingAyah = ayahId.ayahNumber
        )

        val playbackRequest = PlaybackRequest(
                verseRange = range,
                rangeRepetitionCount = 1,
                ayahRepetitionCount = 1,
                recitation = recitations.currentRecitation,
                autoScrollEnabled = false,
                highlightWords = false
        )
        playerControl.play(playbackRequest)
    }

    suspend fun getBookmarkFolders() = bookmarkFoldersRepository.getFoldersList()

    suspend fun bookmarkAyah(ayahId: AyahId) {
        ayahBookmarksRepository.bookmarkAyah(ayahId, BookmarkFolder.DEFAULT_FOLDER_ID)
    }

    suspend fun removeBookmark(ayahId: AyahId) =
            ayahBookmarksRepository.removeAyahBookmark(ayahId)

    suspend fun getEnabledTranslations(): List<Translation> = translationsRepository.getEnabledTranslations()

}
