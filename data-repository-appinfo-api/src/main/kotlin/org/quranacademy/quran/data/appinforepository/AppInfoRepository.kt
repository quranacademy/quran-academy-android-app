package org.quranacademy.quran.data.appinforepository

import org.quranacademy.quran.domain.models.Feedback

interface AppInfoRepository {

    suspend fun isAppUpdateNeeded(): Boolean

    suspend fun sendFeedback(feedback: Feedback)

}