package org.quranacademy.quran.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.QuranConstants
import javax.inject.Inject

class AyahPageFinder @Inject constructor() {

    /**
     * Находит страницу, на которой расположен указанный аят
     */
    suspend fun getPageForAyah(surahNumber: Int, ayahNumber: Int): Int = withContext(Dispatchers.Unconfined) {
        // Термины
        //
        // Целевая сура - сура, номер которой,был передан в surahNumber
        //
        // Логика работы
        //
        // 1) В первую очередь нужно найти страницы, которые занимает
        //    сура (т. е. страницы, на которых сура начинается и заканчивается).
        //    В качестве начальной страницы суры подразумевается страница, на которой
        //    расположен первый аят (т. к. могут быть ситуации, когда заголовок суры расположен на одной странице,
        //    а басмаля и первый аят на следующей).
        //    Номера страниц, на которых начинаются суры указаны в массиве QuranConstants.PAGES_FOR_SURAH
        // 2) Для определения конца суры, берется страница, на которой начинается следующая
        //    сура, и тут может быть несколько вариантов:
        //      1) Начало целевой суры совпадает с началом следующей суры. Это значит, что сура занимает только одну страницу.
        //      2) Следующая сура разделяет страницу с целевой сурой, т. е. конец целевой суры располагается на одной странице
        //         с началом следующей суры. В этом случае мы включаем эту страницу в качестве страницы суры
        //      3) Если сура начинается с новой страницы (т. е. первый аят суры располагается прямо в начале страницы),
        //         то мы не добавляем ее в список страниц суры
        // 3) После того, как мы нашли страницы суры, мы проходимся по каждой странице и проверяем,
        //    находится ли аят на данной странице. Для этого мы берем первый и последний аяты и проверяем, входит ли
        //    в этот промежуток наш аят.
        //    Если страница является последней, а результатов нет, то понятно, что аят находится на ней
        //

        if (surahNumber !in 1..114) {
            throw IllegalArgumentException("Incorrect surah number: $surahNumber")
        }

        val surahPosition = surahNumber - 1
        val surahAyahsNumber = QuranConstants.SURAH_AYAHS_NUMBER[surahPosition]

        if (ayahNumber !in 1..surahAyahsNumber) {
            throw IllegalArgumentException("Incorrect ayah number \"$ayahNumber\" for surah $surahNumber")
        }

        // перый шаг
        val surahStartPage = QuranConstants.PAGES_FOR_SURAH[surahPosition]
        val isLastSurah = surahNumber == QuranConstants.SURAHS_COUNT
        val nextSurahStartPage = if (isLastSurah) -1 else QuranConstants.PAGES_FOR_SURAH[surahPosition + 1]
        val isNextSurahStartsOnTheSamePage = surahStartPage == nextSurahStartPage

        // второй шаг
        if (isLastSurah || isNextSurahStartsOnTheSamePage) {
            return@withContext surahStartPage
        }

        val nextSurahPageStartAyah = QuranConstants.AYAH_FOR_PAGE[nextSurahStartPage - 1]
        val includeNextSurahStartPage = nextSurahPageStartAyah > 1
        val surahPagesCount = nextSurahStartPage - surahStartPage + if (includeNextSurahStartPage) 1 else 0
        val lastSurahPage = surahStartPage + (surahPagesCount - 1)

        // третий шаг
        for (surahPage in surahStartPage..lastSurahPage) {
            val isLastSurahPage = surahPage == lastSurahPage
            if (isLastSurahPage) {
                return@withContext surahPage
            }

            val isFirstSurahPage = surahPage == surahStartPage
            val pageFirstAyahNumber = if (isFirstSurahPage) 1 else QuranConstants.AYAH_FOR_PAGE[surahPage - 1]
            val nextPageFirstAyahNumber = QuranConstants.AYAH_FOR_PAGE[surahPage]
            if (ayahNumber in pageFirstAyahNumber until nextPageFirstAyahNumber) {
                return@withContext surahPage
            }
        }

        //по идее, этого не может быть
        throw RuntimeException("Page not found. Surah: $surahNumber, ayah: $ayahNumber")
    }

}