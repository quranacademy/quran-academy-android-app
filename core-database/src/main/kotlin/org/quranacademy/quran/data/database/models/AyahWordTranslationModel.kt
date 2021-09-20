package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "word_by_word_translation", database = AppDatabase::class, createWithDatabase = false)
class AyahWordTranslationModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "sura")
    var surahNumber: Int = 0

    @Column(name = "ayat")
    var ayahNumber: Int = 0

    @Column(name = "order")
    var wordPosition: Int = 0

    @Column(name = "text")
    var text: String? = null

}