package org.quranacademy.quran.surahdetails.mvp

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.SurahDetails
import javax.inject.Inject

class SurahDetailsUiMapper @Inject constructor(
        private val ayahUiMapper: AyahUiMapper
) {

    fun mapTo(model: SurahDetails): SurahDetailsUiModel {
        val surahPageIndexes = mutableListOf<Int>()
        QuranConstants.SURAH_FOR_PAGE.forEachIndexed { pageIndex, pageSurahNumber ->
            if (pageSurahNumber == model.surahNumber) {
                surahPageIndexes.add(pageIndex)
            } else if (pageSurahNumber > model.surahNumber) {
                //если все страницы суры найдены (началась следующая сура), дальше страницы не ищем
                return@forEachIndexed
            }
        }

        val surahPageFirstAyahs = linkedMapOf<Int, Int>()
        surahPageIndexes.map { pageIndex ->
            val ayahNumber = QuranConstants.AYAH_FOR_PAGE[pageIndex]
            val pageNumber = pageIndex + 1
            surahPageFirstAyahs[ayahNumber] = pageNumber
        }
        val showPageNumber = surahPageFirstAyahs[1] != null
        return SurahDetailsUiModel(
                id = model.id,
                surahNumber = model.surahNumber,
                pageNumber = model.pageNumber,
                showPageNumber = showPageNumber,
                bismillahPre = model.bismillahPre,
                arabicName = model.arabicName,
                transliteratedName = model.transliteratedName,
                translatedName = model.translatedName,
                ayahsCount = model.ayahsCount,
                ayahs = ayahUiMapper.mapTo(model.ayahs, surahPageFirstAyahs)
        )
    }

}