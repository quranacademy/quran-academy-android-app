package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "RecentReadingPlaces", database = AppDatabase::class)
class RecentReadingPlaceModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "surah")
    var surahNumber: Int = 0

    @Column(name = "ayah")
    var ayahNumber: Int = 0

    @Column(name = "is_mushaf_mode")
    var isMushafMode: Boolean = false

    @Column(name = "timestamp")
    var timestamp: Long = 0

    constructor(
            surahNumber: Int,
            ayahNumber: Int,
            isMushafMode: Boolean,
            timestamp: Long
    ) : this() {
        this.surahNumber = surahNumber
        this.ayahNumber = ayahNumber
        this.isMushafMode = isMushafMode
        this.timestamp = timestamp
    }

}