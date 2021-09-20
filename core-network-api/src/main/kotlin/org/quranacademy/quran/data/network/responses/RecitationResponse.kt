package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class RecitationResponse {

    @SerializedName("id")
    var id: Long = 0

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("url_template")
    lateinit var audioUrlTemplate: String

    @SerializedName("timecodes_file")
    val timecodesFile: String? = null

}