package org.quranacademy.quran.surahdetails.mvp

import android.annotation.SuppressLint
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.domain.models.Ayah
import javax.inject.Inject

class AyahUiMapper @Inject constructor() {

    @SuppressLint("LogNotTimber")
    fun mapTo(models: List<Ayah>, surahPageFirstAyahs: LinkedHashMap<Int, Int>): List<AyahUiModel> {
        val surahNumber = models.first().surahNumber
        val firstsPageAyahs = surahPageFirstAyahs.keys.reversed()

        return models.map { ayah ->
            val ayahNumber = ayah.ayahNumber

            val firstPageAyah = firstsPageAyahs.firstOrNull { firstPageAyah ->
                firstPageAyah <= ayahNumber
            }
            val ayahPageNumber = if (firstPageAyah != null) {
                surahPageFirstAyahs[firstPageAyah]!!
            } else {
                //surah doesn't start from start of page
                QuranConstants.PAGES_FOR_SURAH[surahNumber - 1]
            }
            val juzNumber = QuranConstants.getPageJuz(ayahPageNumber)
            //если сура начинается в начале страницы (т. е. первый аят суры стоит
            // в самом начале страницы), то мы показываем номер страницы не над аятом,
            // а над заголовком суры, чтобы не посртить внешний вид
            val showPageAyah = ayah.ayahNumber != 1 && surahPageFirstAyahs.contains(ayahNumber)
            AyahUiModel(
                    surahNumber = ayah.surahNumber,
                    ayahNumber = ayah.ayahNumber,
                    isSajdaAyah = ayah.isSajdaAyah,
                    showPageAyah = showPageAyah,
                    pageNumber = ayahPageNumber,
                    juzNumber = juzNumber,
                    translations = ayah.translations,
                    words = ayah.words,
                    isWordByWordEnabled = ayah.isWordByWordEnabled
            )
        }
    }

}