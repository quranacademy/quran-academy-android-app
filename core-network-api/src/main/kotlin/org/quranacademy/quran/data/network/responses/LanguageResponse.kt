package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class LanguageResponse {

    @SerializedName("code")
    lateinit var code: String

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("name_english")
    lateinit var englishName: String

    @SerializedName("is_rtl")
    var isRtl: Boolean = false

}