package org.quranacademy.quran.domain.models

data class AppInfo(
        val applicationId: String,
        val versionName: String,
        val versionCode: Int,
        val buildId: String,
        val buildType: String,
        val buildTime: String,
        val isDebug: Boolean
) {

    val googlePlayUrl = "https://play.google.com/store/apps/details?id=$applicationId"

}