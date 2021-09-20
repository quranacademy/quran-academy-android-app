package org.quranacademy.quran.settings.domain.mushaftype

import kotlinx.coroutines.coroutineScope
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.domain.repositories.MushafPageBoundsRepository
import org.quranacademy.quran.domain.repositories.QuranImagesRepository
import org.quranacademy.quran.settings.R
import org.quranacademy.quran.settings.presentation.mvp.mushaftype.PageTypeInfo
import javax.inject.Inject

class MushafSelectionInteractor @Inject constructor(
        private val imagesRepository: QuranImagesRepository,
        private val mushafPageBoundsRepository: MushafPageBoundsRepository,
        private val resourcesManager: ResourcesManager
) {

    companion object {
        const val PREVIEW_PAGE_NUMBER = 604
    }

    suspend fun getMushafTypes(): List<PageTypeInfo> = coroutineScope {
        val types = listOf(MushafPageType.MADANI, MushafPageType.MADANI_NEW)
        val titles = listOf(R.string.madani_title, R.string.madani_new_title)
        val descriptions = listOf(R.string.madani_description, R.string.madani_new_description)
        types.mapIndexed { index, type ->
            val image = imagesRepository.getPageImageFile(PREVIEW_PAGE_NUMBER, type)
            val isBoundsDatabaseDownloaded = mushafPageBoundsRepository.isBoundsDatabaseDownloaded(type)
            PageTypeInfo(
                    type = type,
                    previewImageFile = image,
                    title = resourcesManager.getString(titles[index]),
                    description = resourcesManager.getString(descriptions[index]),
                    isBoundsDatabaseDownloaded = isBoundsDatabaseDownloaded
            )
        }
    }

    suspend fun downloadBounds(type: MushafPageType, onProgress: (FileDownloadInfo) -> Unit) {
        mushafPageBoundsRepository.downloadAyahBoundsData(type, onProgress)
    }

    suspend fun selectMushafPageType(type: MushafPageType) {
        imagesRepository.setMushafPageType(type)
        mushafPageBoundsRepository.reconnectToBoundsDatabase()
    }

    suspend fun downloadPages(typeInfos: List<PageTypeInfo>) {
        typeInfos.forEach { info ->
            imagesRepository.downloadPageImage(PREVIEW_PAGE_NUMBER, info.type)
            info.onImageDownloaded()
        }
    }


}
