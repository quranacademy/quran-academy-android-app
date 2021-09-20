package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class LanguagesListResponse {

    @SerializedName("languages")
    lateinit var languages: List<LanguageResponse>

}