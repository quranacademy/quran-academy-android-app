package org.quranacademy.quran.data.database.daos

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel
import org.quranacademy.quran.data.database.models.SurahNameTranslationModel_Table
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Language
import javax.inject.Inject

class SurahsNameTranslationsDao @Inject constructor(
        private val appPreferences: AppPreferences
) {

    private val adapter by lazy { FlowManager.getModelAdapter(SurahNameTranslationModel::class.java) }

    fun getAllSurahNames(): List<SurahNameTranslationModel> {
        return SQLite.select()
                .from(SurahNameTranslationModel::class.java)
                .where(SurahNameTranslationModel_Table.language.`is`(appPreferences.getAppLanguage()))
                .queryList()
    }

    fun saveSurahNames(surahNames: List<SurahNameTranslationModel>) {
        adapter.saveAll(surahNames)
    }

    fun deleteSurahNamesForLanguage(language: Language) {
        SQLite.delete()
                .from(SurahNameTranslationModel::class.java)
                .where(SurahNameTranslationModel_Table.language.`is`(language.code))
                .execute()
    }

    fun getSurahNameByNumber(surahNumber: Int): SurahNameTranslationModel {
        return SQLite.select().from(SurahNameTranslationModel::class.java)
                .where(SurahNameTranslationModel_Table.surah_number.`is`(surahNumber) and
                        SurahNameTranslationModel_Table.language.`is`(appPreferences.getAppLanguage()))
                .querySingle()!!
    }

}