package org.quranacademy.quran.data.network.requests

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
        @SerializedName("email") val email: String?,
        @SerializedName("text") val text: String
) {

    @SerializedName("operating_system")
    val os: String = "android"

}