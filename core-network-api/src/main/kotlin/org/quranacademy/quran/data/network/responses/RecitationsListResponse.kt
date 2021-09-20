package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class RecitationsListResponse {

    @SerializedName("data")
    lateinit var recitations: List<RecitationResponse>

}