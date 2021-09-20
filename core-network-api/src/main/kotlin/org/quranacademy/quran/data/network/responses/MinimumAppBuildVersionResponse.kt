package org.quranacademy.quran.data.network.responses

import com.google.gson.annotations.SerializedName

class MinimumAppBuildVersionResponse {

    @SerializedName("app-version")
    var minimumAppBuildVersion: Int = 0

}