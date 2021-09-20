package org.quranacademy.quran.appinforepository

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.appinforepository.AppInfoRepository
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.data.network.requests.FeedbackRequest
import org.quranacademy.quran.domain.models.AppInfo
import org.quranacademy.quran.domain.models.Feedback
import javax.inject.Inject

class AppInfoRepositoryImpl @Inject constructor(
        private val appInfo: AppInfo,
        private val quranAcademyApi: QuranAcademyApi
) : AppInfoRepository {

    override suspend fun isAppUpdateNeeded(): Boolean = withContext(Dispatchers.IO) {
        val minAppVersionResponse = quranAcademyApi.getMinimumAppBuildVersion()
        minAppVersionResponse.minimumAppBuildVersion > appInfo.versionCode
    }

    override suspend fun sendFeedback(feedback: Feedback) = withContext(Dispatchers.IO) {
        val deviceInfo = "Device: ${Build.BRAND} - ${Build.MODEL}" +
                "\nAndroid:  Android ${Build.VERSION.RELEASE}"


        val resultFeedback = "${feedback.text}\n\n$deviceInfo"

        val feedbackRequest = FeedbackRequest(feedback.email, resultFeedback)
        quranAcademyApi.sendFeedback(feedbackRequest)
    }

}