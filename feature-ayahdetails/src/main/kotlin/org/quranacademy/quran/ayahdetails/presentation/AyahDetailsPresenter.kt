package org.quranacademy.quran.ayahdetails.presentation

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.ayahdetails.domain.AyahDetailsInteractor
import org.quranacademy.quran.bookmarks.ui.BookmarkFoldersScreen
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.sharingdialog.SharingType
import org.quranacademy.quran.sharingdialog.TranslationsSharingScreen
import javax.inject.Inject

@InjectViewState
class AyahDetailsPresenter @Inject constructor(
        private val ayahId: AyahId,
        private val interactor: AyahDetailsInteractor,
        private val radioPlayerSynchronizer: RadioPlayerSynchronizer
) : BasePresenter<AyahDetailsView>() {

    private var isSurahLoaded = false
    private lateinit var ayahDetails: AyahDetails

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadAyah()
    }

    fun onBookmarkAyahClicked(isBookmarked: Boolean) = launch {
        if (isBookmarked) {
            val folders = interactor.getBookmarkFolders()
            if (folders.size > 1) {
                router.goForward(BookmarkFoldersScreen(ayahId))
            } else {
                interactor.bookmarkAyah(ayahId)
            }
        } else {
            interactor.removeBookmark(ayahId)
        }
    }

    fun onShareAyahClicked(sharingType: SharingType) = launch {
        if (!::ayahDetails.isInitialized) return@launch

        val enabledTranslations = interactor.getEnabledTranslations()
        router.goForward(TranslationsSharingScreen(
                type = sharingType,
                translations = enabledTranslations,
                ayahs = listOf(ayahId)
        ))
    }

    fun onPlayAyahClicked() {
        if (radioPlayerSynchronizer.isRadioActive()) {
            radioPlayerSynchronizer.stopRadio {
                onPlayAyahClicked()
            }
            return
        }

        launch {
            interactor.playAyah(ayahId)
            router.goBack()
        }
    }

    private fun loadAyah() = launch {
        viewState.showProgressLayout(true)
        val ayahDetails = interactor.getAyahDetails(ayahId)
        viewState.showProgressLayout(false)
        onAyahDetailsLoaded(ayahDetails)
    }

    private fun onAyahDetailsLoaded(ayahDetails: AyahDetails) {
        isSurahLoaded = true
        this.ayahDetails = ayahDetails
        if (ayahDetails.translations.isNotEmpty()) {
            viewState.showAyahDetails(ayahDetails)
        } else {
            viewState.showTranslationsListEmptyLabel()
        }
    }

}