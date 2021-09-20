package org.quranacademy.quran.quranimagesrepository.urlpovider

import org.quranacademy.quran.data.downloading.QuranEndpointsProvider
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.MushafPageType
import org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints.MadaniNewQuranEndpoints
import org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints.MadaniQuranEndpoints
import org.quranacademy.quran.quranimagesrepository.urlpovider.endpoints.QuranEndpoints
import javax.inject.Inject

class QuranUrlProviderImpl @Inject constructor(
        private val appPreferences: AppPreferences,
        private val madaniNewQuranEndpoints: MadaniNewQuranEndpoints,
        private val madaniQuranEndpoints: MadaniQuranEndpoints
) : QuranEndpointsProvider {

    override fun getMushafBundleUrl(): String {
        return getEndpoints().getMushafBundleUrl()
    }

    override fun getMushafImageUrl(
            pageNumber: Int,
            mushafPageType: MushafPageType
    ): String {
        return getEndpoints(mushafPageType).getMushafImageUrl(pageNumber)
    }

    override fun getMushafAyahBoundsUrl(mushafPageType: MushafPageType): String {
        return getEndpoints(mushafPageType).getMushafAyahBoundsUrl()
    }

    private fun getEndpoints(
            type: MushafPageType = appPreferences.getMushafType()
    ): QuranEndpoints {
        return when (type) {
            MushafPageType.MADANI_NEW -> madaniNewQuranEndpoints
            else -> madaniQuranEndpoints
        }
    }

}