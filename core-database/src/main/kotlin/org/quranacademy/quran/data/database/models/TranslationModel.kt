package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.joda.time.DateTime
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "Translations", database = AppDatabase::class)
class TranslationModel() {

    @PrimaryKey
    @Column(name = "code")
    lateinit var code: String

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "language_code")
    lateinit var languageCode: String

    @Column(name = "file_url")
    lateinit var fileUrl: String

    @Column(name = "file_size")
    var fileSize: Long = -1

    @Column(name = "file_name")
    lateinit var fileName: String

    @Column(name = "is_tafseer")
    var isTafseer: Boolean = false

    @Column(name = "is_downloaded")
    var isDownloaded: Boolean = false

    @Column(name = "is_enabled")
    var isEnabled: Boolean = false

    @Column(name = "remote_last_update_time")
    lateinit var remoteLastUpdateTime: DateTime

    @Column(name = "local_last_update_time")
    var localLastUpdateTime: DateTime? = null

    constructor(
            code: String,
            name: String,
            languageCode: String,
            fileUrl: String,
            fileSize: Long,
            fileName: String,
            isTafseer: Boolean,
            isDownloaded: Boolean = false,
            isEnabled: Boolean = false,
            remoteLastUpdateTime: DateTime
    ) : this() {
        this.code = code
        this.languageCode = languageCode
        this.name = name
        this.fileUrl = fileUrl
        this.fileSize = fileSize
        this.fileName = fileName
        this.isTafseer = isTafseer
        this.isDownloaded = isDownloaded
        this.isEnabled = isEnabled
        this.remoteLastUpdateTime = remoteLastUpdateTime
    }

}