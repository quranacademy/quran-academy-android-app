package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "recitation_timecodes", database = AppDatabase::class, createWithDatabase = false)
class AudioTimecodeModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "surah_number")
    var surahNumber: Int = 0

    @Column(name = "ayah_number")
    var ayahNumber: Int = 0

    @Column(name = "word_number")
    var wordNumber: Int = 0

    @Column(name = "start_time")
    var startTime: Long = 0

    @Column(name = "end_time")
    var endTime: Long = 0

}