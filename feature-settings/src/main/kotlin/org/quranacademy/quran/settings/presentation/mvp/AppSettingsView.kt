package org.quranacademy.quran.settings.presentation.mvp

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.presentation.mvp.BaseMvpView
import org.quranacademy.quran.settings.domain.StoragesInfo

@StateStrategyType(AddToEndSingleStrategy::class)
interface AppSettingsView : BaseMvpView {

    fun showTranslationUpdatesCount(count: Int)

    fun showTafseerUpdatesCount(count: Int)

    fun showWordByWordTranslationUpdatesCount(count: Int)

    fun showReadingHistorySize(size: Int)

    fun showMushafPagesType(type: MushafPageType)

    fun hideImagesBundleDownloadingItem()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showReadingHistorySizeSelectionDialog()

    fun showImagesBundleDownloadProgress(isVisible: Boolean)

    fun updateImagesBundleDownloadProgress(downloadInfo: FileDownloadInfo)

    fun showStorageChoosingPref()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showStorageChoosingDialog(storages: StoragesInfo)

    fun showAppDataTransferProgress(isVisible: Boolean)

}