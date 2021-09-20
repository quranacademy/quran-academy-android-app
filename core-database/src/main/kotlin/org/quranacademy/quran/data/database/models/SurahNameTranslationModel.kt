package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "SurahNameTranslations", database = AppDatabase::class)
class SurahNameTranslationModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "surah_number")
    var surahNumber: Int = 0

    @Column(name = "language")
    lateinit var language: String

    @Column(name = "transliterated_name")
    lateinit var transliteratedName: String

    @Column(name = "translated_name")
    lateinit var translatedName: String

    constructor(
            surahNumber: Int,
            language: String,
            transliteratedName: String,
            translatedName: String
    ) : this() {
        this.surahNumber = surahNumber
        this.language = language
        this.transliteratedName = transliteratedName
        this.translatedName = translatedName
    }

}
