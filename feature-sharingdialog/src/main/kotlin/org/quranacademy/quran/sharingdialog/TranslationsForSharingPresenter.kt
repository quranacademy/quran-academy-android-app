package org.quranacademy.quran.sharingdialog

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.commons.SystemManager
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.extensions.replace
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.sharingdialog.sharingmanager.AyahsTextSharingManager
import javax.inject.Inject
import javax.inject.Named

@InjectViewState
class TranslationsForSharingPresenter @Inject constructor(
        private val sharingType: SharingType,
        @Named("ayahs")
        private val selectedAyahs: List<AyahId>,
        @Named("translations")
        private val translations: List<Translation>,
        private val systemManager: SystemManager,
        private val ayahsTextSharingManager: AyahsTextSharingManager
) : BasePresenter<TranslationsForSharingView>() {

    private val translationUiModels by lazy {
        val items = translations.map {
            TranslationSharingModel(it.code, it.name, false)
        }
        val copyQuranTextTitle = resources.getString(R.string.share_quranarabic_text_option)
        mutableListOf(
                TranslationSharingModel("quran", copyQuranTextTitle, true),
                *items.toTypedArray()
        )
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.showTranslations(translationUiModels)
    }

    fun onTranslationSelected(translation: TranslationSharingModel) {
        translationUiModels.replace(translation, translation.copy(
                isSelected = !translation.isSelected
        )) { old, new -> old.code == new.code }
        viewState.showTranslations(translationUiModels)

        val isSelectedAtLeastOneTranslation = translationUiModels.firstOrNull { it.isSelected } != null
        viewState.updateButtonState(isSelectedAtLeastOneTranslation)
    }

    fun onFinishClicked() = launch {
        val copyQuranArabicText = translationUiModels
                .firstOrNull { it.code == QURAN_TEXT_CODE } != null
        val selectedTranslations = translationUiModels
                .filter { it.isSelected && it.code != QURAN_TEXT_CODE }
                .map { translation ->
                    translations.first { it.code == translation.code }
                }

        val textForSharing = getAyahTextForSharing(
                selectedAyahs,
                selectedTranslations,
                copyQuranArabicText
        )

        if (sharingType == SharingType.COPYING) {
            systemManager.copyText(textForSharing)
            viewState.showMessage(resourcesManager.getString(org.quranacademy.quran.core.ui.R.string.ayah_text_copied))
        } else {
            systemManager.shareText(textForSharing)
        }

        router.goBack()
    }

    private suspend fun getAyahTextForSharing(
            selectedAyahs: List<AyahId>,
            selectedTranslations: List<Translation>,
            copyQuranArabicText: Boolean
    ): String {
        return ayahsTextSharingManager.getAyahsTextForSharing(
                selectedAyahs = selectedAyahs,
                selectedTranslations = selectedTranslations,
                copyQuranArabicText = copyQuranArabicText
        )
    }

    companion object {
        private val QURAN_TEXT_CODE = "quran"
    }

}