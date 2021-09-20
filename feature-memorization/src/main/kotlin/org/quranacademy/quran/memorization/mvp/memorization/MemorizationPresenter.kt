package org.quranacademy.quran.memorization.mvp.memorization

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.RadioPlayerSynchronizer
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.memorization.domain.MemorizationInteractor
import org.quranacademy.quran.memorization.models.MemorizationOptions
import org.quranacademy.quran.memorization.mvp.memorization.gamemanager.MemorizationGameManager
import org.quranacademy.quran.memorization.routing.MemorizationOptionsScreen
import org.quranacademy.quran.memorization.routing.MemorizationScreen
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import java.util.*
import javax.inject.Inject

@InjectViewState
class MemorizationPresenter @Inject constructor(
        private val memorizationOptions: MemorizationOptions,
        private val interactor: MemorizationInteractor,
        private val gameManager: MemorizationGameManager,
        private val radioPlayerSynchronizer: RadioPlayerSynchronizer
) : BasePresenter<MemorizationView>() {

    private var isToolbarVisible = true
    private val pageInfos = mutableMapOf<Int, QuranPage>()
    private var isPlayerPausedByExitDialog = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        gameManager.view = object : MemorizationGameManager.View {

            override fun showDataDownloadDialog(isVisible: Boolean) {
                viewState.showDataDownloadDialog(isVisible)
            }

            override fun updateHighlights(highlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>) {
                viewState.updateAyahHighlighting(highlights)
            }

            override fun showPage(pageNumber: Int) {
                launch { viewState.showPage(getPageInfo(pageNumber)) }
            }

            override fun updatePlayPauseState(isPlaying: Boolean) {
                viewState.updatePlayPauseState(isPlaying)
            }

            override fun onMemorizationCompleted() {
                viewState.showMemorizationCompleted()
            }

        }

        checkRadioAndPlayerStopped {
            startMemorization()
        }
    }

    fun retryDownloadClicked() {
        startMemorization()
    }

    fun onPageClicked() {
        isToolbarVisible = !isToolbarVisible
        viewState.showToolbar(isToolbarVisible)
    }

    fun onPlayPauseClicked() {
        val isPlaying = gameManager.isAudioPlaying()
        if (isPlaying) gameManager.pauseAudio() else gameManager.playAudio()
    }

    fun onBackPressed() {
        if (gameManager.isAudioPlaying()) {
            gameManager.pauseAudio()
            isPlayerPausedByExitDialog = true
        }
        viewState.showExitConfirmDialog()
    }

    fun onExitConfirmed() {
        gameManager.stopAudio()
        router.replace(MemorizationOptionsScreen(memorizationOptions))
    }

    fun onExitCanceled() {
        if (isPlayerPausedByExitDialog) {
            gameManager.playAudio()
            isPlayerPausedByExitDialog = false
        }
    }

    fun onRestartMemorizationClicked() {
        router.replace(MemorizationScreen(memorizationOptions))
    }

    fun onFinishMemorizationClicked() {
        gameManager.stopAudio()
        router.replace(MemorizationOptionsScreen())
    }


    private fun checkRadioAndPlayerStopped(onReady: () -> Unit) {
        if (radioPlayerSynchronizer.isPlayerActive()) {
            radioPlayerSynchronizer.stopPlayer(onReady)
            return
        }

        if (radioPlayerSynchronizer.isRadioActive()) {
            radioPlayerSynchronizer.stopRadio(onReady)
            return
        }

        onReady()
    }

    private fun startMemorization() = launch {
        try {
            gameManager.startMemorization(memorizationOptions)
        } catch (error: NoNetworkException) {
            viewState.showDataDownloadDialog(false)
            viewState.showNoNetworkDialog()
        } catch (error: AudioDownloadException.NoNetwork) {
            viewState.showDataDownloadDialog(false)
            viewState.showNoNetworkDialog()
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    private suspend fun getPageInfo(pageNumber: Int): QuranPage {
        return pageInfos[pageNumber]
                ?: interactor.getPageInfo(pageNumber).also { pageInfos[pageNumber] = it }
    }

}