package org.quranacademy.quran.player.data.quranplayer.timecodes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.quranacademy.quran.data.database.fillModels
import org.quranacademy.quran.data.database.models.AudioTimecodeModel
import org.quranacademy.quran.data.database.models.AudioTimecodeModel_Table

class TimecodesDatabaseAdapter(
        context: Context, filePath: String
) : SQLiteOpenHelper(context, filePath, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)

        db.rawQuery("pragma journal_mode=memory;", null).close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun getSurahTimecodes(surahNumber: Int): List<AudioTimecodeModel> {
        val query = SQLite.select().from(AudioTimecodeModel::class.java)
                .where(AudioTimecodeModel_Table.surah_number.`is`(surahNumber))
                .orderBy(AudioTimecodeModel_Table.ayah_number, true)
                .toString()

        val timeCodesCursor = readableDatabase.rawQuery(query, null)
        return timeCodesCursor.fillModels()
    }

}