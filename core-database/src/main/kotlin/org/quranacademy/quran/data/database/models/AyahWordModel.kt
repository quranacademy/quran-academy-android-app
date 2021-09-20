package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(database = AppDatabase::class, name = "words", createWithDatabase = false)
class AyahWordModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "surah")
    var surahNumber: Int = 0

    @Column(name = "ayah")
    var ayahNumber: Int = 0

    @Column(name = "order")
    var position: Int = 0

    @Column(name = "text")
    lateinit var arabicText: String

    @Column(name = "text_tajweed")
    lateinit var arabicTextTajweed: String

}