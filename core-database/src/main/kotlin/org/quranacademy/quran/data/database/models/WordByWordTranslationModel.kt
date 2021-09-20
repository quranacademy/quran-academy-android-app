package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.joda.time.DateTime
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "WordByWordTranslations", database = AppDatabase::class)
class WordByWordTranslationModel() {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "language_code")
    lateinit var languageCode: String

    @Column(name = "file_url")
    lateinit var fileUrl: String

    @Column(name = "file_name")
    lateinit var fileName: String

    @Column(name = "is_downloaded")
    var isDownloaded: Boolean = false

    @Column(name = "remote_last_update_time")
    lateinit var remoteLastUpdateTime: DateTime

    @Column(name = "local_last_update_time")
    var localLastUpdateTime: DateTime? = null

    constructor(
            id: Long = 0,
            name: String,
            languageCode: String,
            fileUrl: String,
            fileName: String,
            isDownloaded: Boolean = false,
            remoteLastUpdateTime: DateTime
    ) : this() {
        this.id = id
        this.name = name
        this.languageCode = languageCode
        this.fileUrl = fileUrl
        this.fileName = fileName
        this.isDownloaded = isDownloaded
        this.remoteLastUpdateTime = remoteLastUpdateTime
    }

}