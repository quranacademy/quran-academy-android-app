package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "BookmarkFolders", database = AppDatabase::class)
class BookmarkFolderModel() : BaseModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "timestamp")
    var timestamp: Long = 0

    constructor(
            id: Long = 0L,
            name: String,
            timestamp: Long
    ) : this() {
        this.id = id
        this.name = name
        this.timestamp = timestamp
    }

}