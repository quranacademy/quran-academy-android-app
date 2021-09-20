package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "ayahs_search_text", database = AppDatabase::class, createWithDatabase = false)
class SearchResultModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "surah")
    var surahNumber: Int = 0

    @Column(name = "ayah")
    var ayahNumber: Int = 0

    @Column(name = "text")
    lateinit var text: String

}