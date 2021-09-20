package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "Languages", database = AppDatabase::class)
class LanguageModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "code")
    lateinit var code: String

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "english_name")
    var englishName: String = ""

    @Column(name = "is_rtl")
    var isRtl: Boolean = false

    @Column(name = "is_downloaded")
    var isDownloaded: Boolean = false

    constructor(
            code: String,
            name: String,
            englishName: String,
            isRtl: Boolean = false,
            isDownloaded: Boolean
    ) : this() {
        this.code = code
        this.name = name
        this.englishName = englishName
        this.isRtl = isRtl
        this.isDownloaded = isDownloaded
    }

}