package org.quranacademy.quran.player.presentation.playeroptions.reciterselection

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsShell
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.recitationsrepository.RecitationsRepository
import javax.inject.Inject

@InjectViewState
class RecitationSelectionPresenter @Inject constructor(
        private val recitationsRepository: RecitationsRepository,
        private val appPreferences: AppPreferences,
        private val playerOptionsShell: PlayerOptionsShell
) : BasePresenter<RecitationSelectionView>() {

    private lateinit var recitations: List<Recitation>

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            val recitationsList = recitationsRepository.getRecitations()
            recitations = recitationsList.recitations
            viewState.showRecitations(recitations)
        }
    }

    fun onRecitationSelected(recitation: Recitation) {
        appPreferences.setCurrentRecitationId(recitation.id)
        playerOptionsShell.onRecitationSelected(recitation)
        router.goBack()
    }

    fun onSearchInput(query: String) {
        if (query.isNotBlank()) {
            val filteredRecitations = recitations.filter {
                it.name.contains(query, true)
            }
            viewState.showRecitations(filteredRecitations)
        } else {
            viewState.showRecitations(recitations)
        }
    }

}