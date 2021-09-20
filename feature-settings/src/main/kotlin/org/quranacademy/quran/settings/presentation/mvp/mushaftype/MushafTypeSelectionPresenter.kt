package org.quranacademy.quran.settings.presentation.mvp.mushaftype

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.domain.exceptions.NoNetworkException
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.settings.domain.mushaftype.MushafSelectionInteractor
import javax.inject.Inject

@InjectViewState
class MushafTypeSelectionPresenter @Inject constructor(
        private val interactor: MushafSelectionInteractor,
        private val navigator: Navigator
) : BasePresenter<MushafTypeSelectionView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            val typeInfos = interactor.getMushafTypes()
            viewState.showPageTypes(typeInfos)

            downloadImages(typeInfos)
        }
    }

    fun onPageTypeSelected(info: PageTypeInfo) = launch {
        if (!info.isBoundsDatabaseDownloaded) {
            try {
                viewState.showMushafBoundsDownloadDialog(true)
                interactor.downloadBounds(info.type) {
                    viewState.updateMushafBoundsDownloadProgress(it)
                }
                viewState.showMushafBoundsDownloadDialog(false)
            } catch (error: Exception) {
                viewState.showBoundsDownloadRetryDialog(info)
                return@launch
            }
        }

        interactor.selectMushafPageType(info.type)
        navigator.finish()
    }

    fun onRetryBundleDownloadingClicked(info: PageTypeInfo) {
        onPageTypeSelected(info)
    }

    private suspend fun downloadImages(typeInfos: List<PageTypeInfo>) {
        try {
            typeInfos.filter {
                !it.isImageDownloaded()
            }.let {
                interactor.downloadPages(it)
            }
        } catch (error: NoNetworkException) {

        } catch (error: Exception) {

        }
    }

}