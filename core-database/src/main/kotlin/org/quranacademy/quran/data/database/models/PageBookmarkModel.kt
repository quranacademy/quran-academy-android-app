package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "PageBookmarks", database = AppDatabase::class)
class PageBookmarkModel() : BaseModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "page_number")
    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    var pageNumber: Int = 0

    @Column(name = "timestamp")
    var timestamp: Long = 0

    constructor(
            id: Long = 0,
            pageNumber: Int,
            timestamp: Long
    ) : this() {
        this.id = id
        this.pageNumber = pageNumber
        this.timestamp = timestamp
    }

}