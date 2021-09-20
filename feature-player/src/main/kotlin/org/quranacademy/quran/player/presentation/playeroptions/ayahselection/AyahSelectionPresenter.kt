package org.quranacademy.quran.player.presentation.playeroptions.ayahselection

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.player.R
import org.quranacademy.quran.player.domain.playeroptions.AyahSelectionInteractor
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahSelectionScreen
import javax.inject.Inject

@InjectViewState
class AyahSelectionPresenter @Inject constructor(
        private val type: AyahSelectionScreen.Type,
        private var initialAyah: AyahId,
        private val interactor: AyahSelectionInteractor
) : BasePresenter<AyahSelectionView>() {

    private lateinit var surahsList: List<Surah>
    private var selectedSurahNumber: Int = initialAyah.surahNumber
    private var selectedAyah: Int = initialAyah.ayahNumber

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            val titleResId = if (type == AyahSelectionScreen.Type.START) {
                R.string.start_playback_ayah_label
            } else R.string.end_playback_ayah_label
            viewState.setTitle(resourcesManager.getString(titleResId))

            surahsList = interactor.getSurahsList()
            val selectedSurah = surahsList.first { it.surahNumber == selectedSurahNumber }
            viewState.showInitialData(
                    surahs = surahsList,
                    ayahsCount = surahsList[selectedSurahNumber - 1].ayahsCount,
                    currentSurah = selectedSurah,
                    currentAyahNumber = selectedAyah
            )
        }
    }

    fun onSurahSelected(surahNumber: Int) {
        this.selectedSurahNumber = surahNumber
        viewState.updateAyahValues(surahsList[surahNumber - 1].ayahsCount)
    }

    fun onAyahSelected(selectedAyah: Int) {
        this.selectedAyah = selectedAyah
    }

    fun onOkButtonClicked() {
        router.goBackWithResult(AyahSelectionScreen.Result(AyahId(selectedSurahNumber, selectedAyah), type))
    }

}