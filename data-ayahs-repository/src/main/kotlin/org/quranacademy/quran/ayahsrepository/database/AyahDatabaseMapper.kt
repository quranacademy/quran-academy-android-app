package org.quranacademy.quran.ayahsrepository.database

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.models.AyahModel
import org.quranacademy.quran.domain.models.Ayah
import org.quranacademy.quran.extensions.normalizeHarfsInText
import javax.inject.Inject

class AyahDatabaseMapper @Inject constructor() {

    fun map(models: List<AyahModel>): List<Ayah> {
        return models.map { surahModel ->
            val isSajdaAyah = QuranConstants.SAJDA_PLACES.any { sajdaPlace ->
                sajdaPlace.surahNumber == surahModel.surahNumber
                        && sajdaPlace.ayahNumber == surahModel.ayahNumber
            }
            Ayah(
                    surahNumber = surahModel.surahNumber,
                    ayahNumber = surahModel.ayahNumber,
                    arabicText = surahModel.arabicText.normalizeHarfsInText(),
                    arabicTextNewUthmanicHafs = surahModel.arabicTextUthmanic,
                    arabicTextTajweed = surahModel.arabicTextTajweed.normalizeHarfsInText(),
                    isSajdaAyah = isSajdaAyah
            )
        }
    }

}