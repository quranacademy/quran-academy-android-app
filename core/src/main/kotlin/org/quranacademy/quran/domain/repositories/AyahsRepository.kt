package org.quranacademy.quran.domain.repositories

import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId

interface AyahsRepository {

    suspend fun getAyahDetails(
            ayahId: AyahId,
            loadTranslations: Boolean = true,
            loadAllTranslations: Boolean = true
    ): AyahDetails

}