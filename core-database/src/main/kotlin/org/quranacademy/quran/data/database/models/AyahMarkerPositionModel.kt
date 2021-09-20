package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "ayah_markers", database = AppDatabase::class, createWithDatabase = false)
class AyahMarkerPositionModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "sura_number")
    var surahNumber: Int = 0

    @Column(name = "ayah_number")
    var ayahNumber: Int = 0

    @Column(name = "page_number")
    var pageNumber: Int = 0

    @Column(name = " x")
    var x: Int = 0

    @Column(name = "y")
    var y: Int = 0

}