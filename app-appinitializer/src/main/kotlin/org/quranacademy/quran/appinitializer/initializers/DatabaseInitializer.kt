package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import com.raizlabs.android.dbflow.config.FlowManager
import net.sqlcipher.database.SQLiteDatabase
import org.quranacademy.quran.appinitializer.AppInitializerElement
import org.quranacademy.quran.data.database.adapters.AyahsArabicDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.MushafPageBoundsDatabaseAdapter
import org.quranacademy.quran.data.database.adapters.WordByWordTranslationDatabaseManager
import javax.inject.Inject

class DatabaseInitializer @Inject constructor(
        private val wordByWordTranslationDatabaseManager: WordByWordTranslationDatabaseManager,
        private val mushafPageBoundsDatabaseAdapter: MushafPageBoundsDatabaseAdapter,
        private val ayahsArabicDatabaseAdapter: AyahsArabicDatabaseAdapter
) : AppInitializerElement {

    override fun initialize(context: Context) {
        SQLiteDatabase.loadLibs(context)
        FlowManager.init(context)

        ayahsArabicDatabaseAdapter.connect()
        wordByWordTranslationDatabaseManager.connect()
        mushafPageBoundsDatabaseAdapter.connect()
    }

}