package org.quranacademy.quran.surahsrepository

import org.quranacademy.quran.domain.models.Ayah
import org.quranacademy.quran.domain.models.Surah
import org.quranacademy.quran.domain.models.SurahDetails
import javax.inject.Inject

class SurahDetailsMapper @Inject constructor() {

    fun map(surah: Surah, ayahs: List<Ayah>): SurahDetails {
        return SurahDetails(
                id = surah.id,
                surahNumber = surah.surahNumber,
                pageNumber = surah.pageNumber,
                bismillahPre = surah.bismillahPre,
                arabicName = surah.arabicName,
                transliteratedName = surah.transliteratedName,
                translatedName = surah.translatedName,
                ayahsCount = surah.ayahsCount,
                ayahs = ayahs
        )
    }

}