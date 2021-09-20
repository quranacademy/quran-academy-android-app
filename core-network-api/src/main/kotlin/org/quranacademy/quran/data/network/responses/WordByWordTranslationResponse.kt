package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class WordByWordTranslationResponse(
        @SerializedName("language_name") val languageName: String,
        @SerializedName("language_code") val languageCode: String,
        @SerializedName("file_url") val fileUrl: String,
        @SerializedName("order") val order: Int,
        @SerializedName("translation_updated_at") val lastUpdateTime: DateTime
)