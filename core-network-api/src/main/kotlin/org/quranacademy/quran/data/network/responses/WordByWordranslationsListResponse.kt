package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class WordByWordranslationsListResponse {

    @SerializedName("data")
    lateinit var translations: List<WordByWordTranslationResponse>

}