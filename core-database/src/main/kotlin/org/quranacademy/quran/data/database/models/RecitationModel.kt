package org.quranacademy.quran.data.database.models

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.quranacademy.quran.data.database.AppDatabase

@Table(name = "recitations", database = AppDatabase::class)
class RecitationModel() {

    @PrimaryKey
    var id: Long = 0

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "audio_url_pattern")
    lateinit var audioUrlTemplate: String

    @Column(name = "timecode_file")
    var timecodesFileUrl: String? = null

    constructor(
            id: Long,
            name: String,
            audioUrlTemplate: String,
            timecodesFile: String?
    ) : this() {
        this.id = id
        this.name = name
        this.audioUrlTemplate = audioUrlTemplate
        this.timecodesFileUrl = timecodesFile
    }

}