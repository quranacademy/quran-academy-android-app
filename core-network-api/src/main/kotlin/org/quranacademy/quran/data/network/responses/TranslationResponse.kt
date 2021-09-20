package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class TranslationResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String,
        @SerializedName("order") val order: Int,
        @SerializedName("language_code") val languageCode: String,
        @SerializedName("file_url") val fileUrl: String,
        @SerializedName("file_size") val fileSize: Long,
        @SerializedName("is_tafseer") val isTafseer: Boolean,
        @SerializedName("translation_updated_at") val lastUpdateTime: DateTime
)