package org.quranacademy.quran.languagesmanager.presentation.mvp

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.exceptions.NoSpaceLeftException
import org.quranacademy.quran.domain.models.Language
import org.quranacademy.quran.domain.models.LanguagesWrapper
import org.quranacademy.quran.languagesmanager.R
import org.quranacademy.quran.languagesmanager.domain.LanguagesInteractor
import org.quranacademy.quran.presentation.extensions.replace
import org.quranacademy.quran.presentation.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class LanguagesManagerPresenter @Inject constructor(
        private val interactor: LanguagesInteractor,
        private val modelMapper: LanguageUIModelMapper
) : BasePresenter<LanguagesManagerView>() {

    private lateinit var languages: MutableList<LanguageUIModel>
    private var currentLanguage: LanguageUIModel? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadLanguagesList()
    }

    fun onRetryLanguagesListLoadingClicked() {
        viewState.showNoNetworkLayout(false)
        loadLanguagesList()
    }

    fun onDownloadLanguageClicked(language: LanguageUIModel) = launch {
        try {
            viewState.showLanguageDownloadProgress()
            interactor.downloadLanguage(language.fromUiModel())
            onDownloadLanguageSuccess(language)
        } catch (error: Exception) {
            when (error) {
                is NoSpaceLeftException -> viewState.showMessage(resources.getString(R.string.not_enough_free_space_on_disk_message))
                else -> errorHandler.proceed(error) { viewState.showMessage(it) }
            }
        } finally {
            viewState.hideLanguageDownloadProgress()
        }
    }

    fun onEnableLanguageClicked(language: LanguageUIModel) = launch {
        interactor.enableLanguage(language.fromUiModel())
        setCurrentLanguage(language)
        showLanguages(true)
    }

    fun onRemoveLanguageClicked(language: LanguageUIModel) = launch {
        try {
            interactor.removeLanguage(language.fromUiModel())
            val newLanguageState = language.copy(isDownloaded = false)
            languages.replace(language, newLanguageState)
            viewState.showMessage(resources.getString(R.string.language_is_removed_message))
            showLanguages(true)
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private fun loadLanguagesList() = launch {
        try {
            viewState.showProgressLayout(true)
            val languages = interactor.getLanguages()
            viewState.showProgressLayout(false)
            onLanguagesListLoaded(languages)
        } catch (error: Exception) {
            when (error) {
                is NoNetworkException -> viewState.showNoNetworkLayout(true)
                else -> errorHandler.proceed(error) { viewState.showMessage(it) }
            }
        }
    }

    private fun onLanguagesListLoaded(languagesWrapper: LanguagesWrapper) {
        val currentLanguageCode = languagesWrapper.currentLanguageCode
        languages = modelMapper.mapToViewModel(languagesWrapper.languages, currentLanguageCode).toMutableList()
        currentLanguage = languages.firstOrNull { it.code == currentLanguageCode }
        showLanguages()
    }

    private fun showLanguages(update: Boolean = false) {
        val downloadedLanguages = languages.filter { it.isDownloaded }
        val downloadedTitle = resources.getString(R.string.downloaded_category_label)

        val downloadedCategory = LanguageCategoryViewModel(downloadedLanguages, downloadedTitle)

        val availableLanguages = languages.filter { !it.isDownloaded }
        val availableTitle = resources.getString(R.string.available_category_label)
        val availableCategory = LanguageCategoryViewModel(availableLanguages, availableTitle)

        val languageCategories = listOf(downloadedCategory, availableCategory)

        if (update) {
            viewState.updateLanguages(languageCategories)
        } else {
            viewState.showLanguages(languageCategories)
        }
    }

    private fun onDownloadLanguageSuccess(language: LanguageUIModel) {
        val languageCopy = language.copy(isDownloaded = true, isEnabled = true)
        replaceLanguage(language, languageCopy)
        setCurrentLanguage(languageCopy)
        showLanguages(true)
    }

    private fun setCurrentLanguage(language: LanguageUIModel) {
        val currentLanguage = this.currentLanguage
        if (currentLanguage != null) {
            val currentLanguageCopy = currentLanguage.copy(isEnabled = false)
            replaceLanguage(currentLanguage, currentLanguageCopy)
        }
        val languageCopy = language.copy(isEnabled = true)
        replaceLanguage(language, languageCopy)
        this.currentLanguage = languageCopy
    }

    private fun replaceLanguage(old: LanguageUIModel, new: LanguageUIModel) {
        languages.replace(old, new) { o1, o2 -> o1.code == o2.code }
    }

    private fun LanguageUIModel.fromUiModel(): Language = modelMapper.mapFromViewModel(this)

}