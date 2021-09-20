package org.quranacademy.quran.data.database

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.database.daos.SurahsNameTranslationsDao
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import org.quranacademy.quran.data.prefs.AppPreferences
import javax.inject.Inject

class SurahNameProvider @Inject constructor(
        appPreferences: AppPreferences,
        surahsNameTranslationsDao: SurahsNameTranslationsDao
) {

    private var surahsList: List<SurahNameTranslationModel> = surahsNameTranslationsDao.getAllSurahNames()

    init {
        GlobalScope.launch {
            appPreferences.getLanguageUpdates()
                    .collect {
                        surahsList = surahsNameTranslationsDao.getAllSurahNames()
                    }
        }
    }

    fun getSurahName(surahNumber: Int): String = surahsList[surahNumber - 1].transliteratedName

}