package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class SurahsListResponse {

    @SerializedName("data")
    lateinit var surahs: List<SurahResponse>

}