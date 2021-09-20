package org.quranacademy.quran.memorization.mvp.options

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.di.PrimitiveWrapper
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.memorization.domain.MemorizationOptionsInteractor
import org.quranacademy.quran.memorization.models.*
import org.quranacademy.quran.memorization.routing.MemorizationScreen
import org.quranacademy.quran.memorization.routing.MemorizationTutorialScreen
import org.quranacademy.quran.presentation.mvp.BasePresenter
import javax.inject.Inject
import javax.inject.Named

@InjectViewState
class MemorizationOptionsPresenter @Inject constructor(
        @Named("options")
        private val initialMemorizationOptions: PrimitiveWrapper<MemorizationOptions?>,
        private val interactor: MemorizationOptionsInteractor
) : BasePresenter<MemorizationOptionsView>() {

    private lateinit var memorizationOptions: MemorizationOptions
    private lateinit var recitations: List<Recitation>
    private lateinit var surahs: List<Surah>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch { initMemorizationOptions() }
    }

    fun onSelectMemorizationModeClicked() {
        val modes = listOf(MemorizationMode.PAGE, MemorizationMode.SURAH)
        viewState.showMemorizationModesSelect(modes)
    }

    fun onMemorizationModeSelected(mode: MemorizationMode) {
        val modeData: ModeData = if (mode == MemorizationMode.PAGE) {
            PageModeData(1)
        } else {
            SurahModeData(1, Pair(1, 7))
        }
        memorizationOptions = memorizationOptions.copy(mode = mode, modeData = modeData)
        viewState.updateMemorizationMode(mode)
        updateModeDataInfo()
    }

    fun onSelectQuranPageClicked() {
        viewState.showQuranPageSelection()
    }

    fun onQuranPageSelected(pageNumber: Int) {
        val data = getPageModeData().copy(pageNumber = pageNumber)
        memorizationOptions = memorizationOptions.copy(modeData = data)
        updateModeDataInfo()
    }

    fun onSelectSurahClicked() {
        viewState.showSurahSelection(surahs)
    }

    fun onSurahSelected(surah: Surah) {
        val surahAyahsCount = interactor.getSurahAyahsCount(surah.surahNumber)
        val data = SurahModeData(surah.surahNumber, Pair(1, surahAyahsCount))
        memorizationOptions = memorizationOptions.copy(modeData = data)
        updateModeDataInfo()
    }

    fun onSelectAyahsRangeClicked() {
        val data = getAyahsRangeModeData()
        val maxAyahValue = interactor.getSurahAyahsCount(data.surahNumber)
        viewState.showAyahsRangeSelection(data.ayahsRange, maxAyahValue)
    }

    fun onAyahsRangeSelected(range: Pair<Int, Int>) {
        val data = getAyahsRangeModeData().copy(ayahsRange = range)
        memorizationOptions = memorizationOptions.copy(modeData = data)
        updateModeDataInfo()
    }

    fun onSelectRepetitionsCountClicked() = launch {
        val values = (3..30).toList()
        viewState.showRepetitionsSelectionDialog(values)
    }

    fun onRepetitionsCountSelected(count: Int) {
        memorizationOptions = memorizationOptions.copy(repetitionsCount = count)
        viewState.updateRepetitionsValue(count)
    }

    fun onSelectDelayBetweenRepetitionsClicked() = launch {
        val values = (1..150).map {
            it * 200 //selection step - 200 ms.
        }
        viewState.showDelayBetweenRepetitionsDialog(values)
    }

    fun onDelayBetweenRepetitionsSelected(delay: Int) {
        memorizationOptions = memorizationOptions.copy(delayBetweenRepetitions = delay)
        viewState.updateDelayBetweenRepetitionsValue(delay)
    }

    fun onSelectRecitationButtonClicked() {
        viewState.showRecitationSelectDialog(recitations)
    }

    fun onRecitationSelected(recitation: Recitation) {
        memorizationOptions = memorizationOptions.copy(recitation = recitation)
        viewState.updateRecitationValue(recitation.name)
    }

    fun onStartMemorizationClicked() {
        router.replace(MemorizationScreen(memorizationOptions))
    }

    fun onShowMemorizationHelpClicked() {
        router.replace(MemorizationTutorialScreen())
    }

    private suspend fun initMemorizationOptions() = launch {
        //viewState.showInitProgressBar(true)
        val recitationsWrapper = interactor.getRecitations()
        surahs = interactor.getSurahs()
        recitations = recitationsWrapper.recitations
        memorizationOptions = initialMemorizationOptions.value ?: MemorizationOptions(
                mode = MemorizationMode.PAGE,
                modeData = PageModeData(1),
                repetitionsCount = 3,
                delayBetweenRepetitions = 200,
                recitation = recitationsWrapper.currentRecitation
        )

        viewState.updateMemorizationMode(memorizationOptions.mode)
        updateModeDataInfo()
        viewState.updateRepetitionsValue(memorizationOptions.repetitionsCount)
        viewState.updateDelayBetweenRepetitionsValue(memorizationOptions.delayBetweenRepetitions)
        viewState.updateRecitationValue(memorizationOptions.recitation.name)
    }

    private fun updateModeDataInfo() {
        if (memorizationOptions.mode == MemorizationMode.PAGE) {
            val data = getPageModeData()
            viewState.updatePageValue(data.pageNumber)
        } else {
            val data = getAyahsRangeModeData()
            viewState.updateSurahValue(data.surahNumber, getSelectedSurahName(data.surahNumber))
            viewState.updateAyahsRangeValue(data.ayahsRange)
        }
    }

    private fun getPageModeData() = memorizationOptions.modeData as PageModeData

    private fun getAyahsRangeModeData() = memorizationOptions.modeData as SurahModeData

    private fun getSelectedSurahName(surahNumber: Int) = surahs[surahNumber - 1].transliteratedName

}