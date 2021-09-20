package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

import org.quranacademy.quran.domain.models.AyahId

class ControllerEnvironment(
        val ayahsRangeFinder: suspend (first: AyahId, AyahId: AyahId) -> List<AyahId>,
        val viewController: MushafControllerView
) {

    val selectedAyahs: MutableList<AyahId> = mutableListOf()
    var ayahToolbarPageNumber: Int = 0
    var isTranslationsShowing: Boolean = false

    fun selectedAyahs() = selectedAyahs.toList()

    suspend fun getAyahs(
            first: AyahId,
            last: AyahId,
            onAyahLoaded: suspend (ayahs: List<AyahId>) -> Unit
    ) {
        try {
            val ayahDetails = ayahsRangeFinder(first, last)
            onAyahLoaded(ayahDetails)
        } catch (error: Exception) {
            viewController.onError(error)
        }
    }

}