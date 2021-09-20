package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "translation", database = AppDatabase::class, createWithDatabase = false)
class AyahTranslationModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "sura")
    var surah: Int = 0

    @Column(name = "ayat")
    var ayah: Int = 0

    @Column(name = "text")
    lateinit var text: String

    @Column(name = "is_new_format")
    var isNewFormat: Boolean = false

}