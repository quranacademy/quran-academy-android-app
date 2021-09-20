package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class TranslationsListResponse {

    @SerializedName("data")
    lateinit var translations: List<TranslationResponse>

}