package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "glyphs", database = AppDatabase::class, createWithDatabase = false)
class AyahBoundsModel {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "page_number")
    var pageNumber: Int = 0

    @Column(name = "line_number")
    var lineNumber: Int = 0

    @Column(name = "sura_number")
    var suraNumber: Int = 0

    @Column(name = "ayah_number")
    var ayahNumber: Int = 0

    @Column(name = "position")
    var position: Int = 0

    @Column(name = "min_x")
    var minX: Int = 0

    @Column(name = "min_y")
    var minY: Int = 0

    @Column(name = "max_x")
    var maxX: Int = 0

    @Column(name = "max_y")
    var maxY: Int = 0

}