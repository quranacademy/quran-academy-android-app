package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.*
import org.quranacademy.quran.data.database.AppDatabase

@Table(
        name = "AyahBookmarks",
        database = AppDatabase::class,
        uniqueColumnGroups = [
            UniqueGroup(
                    groupNumber = 1,
                    uniqueConflict = ConflictAction.REPLACE
            )
        ]
)
class AyahBookmarkModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "surah_number")
    @Unique(uniqueGroups = [1], unique = false)
    var surahNumber: Int = 0

    @Column(name = "ayah_number")
    @Unique(uniqueGroups = [1], unique = false)
    var ayahNumber: Int = 0

    @Column(name = "timestamp")
    var timestamp: Long = 0

    @Column(name = "folder_id")
    var folderId: Long? = 0

    constructor(
            id: Long = 0,
            surahNumber: Int,
            ayahNumber: Int,
            timestamp: Long,
            folderId: Long?
    ) : this() {
        this.id = id
        this.surahNumber = surahNumber
        this.ayahNumber = ayahNumber
        this.timestamp = timestamp
        this.folderId = folderId
    }

}