package org.quranacademy.quran.recitationsrepository.recitations

import org.joda.time.DateTime
import org.joda.time.Days
import org.quranacademy.quran.data.database.daos.RecitationsDao
import org.quranacademy.quran.data.database.models.RecitationModel
import org.quranacademy.quran.data.network.QuranAcademyApi
import org.quranacademy.quran.data.prefs.AppPreferences
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.RecitationsList
import javax.inject.Inject

class RecitationsDataSource @Inject constructor(
        private val quranAcademyApi: QuranAcademyApi,
        private val appPreferences: AppPreferences,
        private val recitationsDao: RecitationsDao,
        private val recitationsResponseMapper: RecitationsResponseMapper,
        private val recitationsDatabaseMapper: RecitationsDatabaseMapper
) {

    companion object {
        const val RECITATIONS_LIST_UPDATE_PERIOD_DAYS = 7
    }

    suspend fun getRecitation(recitationId: Long): Recitation {
        val recitationModel = recitationsDao.getRecitation(recitationId)
        return recitationsDatabaseMapper.mapFrom(recitationModel)
    }

    suspend fun getRecitations(loadFromInternet: Boolean = false): RecitationsList {
        val recitationsUpdateTime = appPreferences.getRecitationsListUpdateTime()
        val isRecitationsListExpired = if (recitationsUpdateTime != null) {
            val currentTime = DateTime.now()
            val periodAfterLastUpdateTime = Days.daysBetween(recitationsUpdateTime, currentTime).days
            periodAfterLastUpdateTime >= RECITATIONS_LIST_UPDATE_PERIOD_DAYS
        } else {
            true
        }
        val recitationModels = if (isRecitationsListExpired || loadFromInternet) {
            downloadRecitationsFromServer()
        } else {
            getRecitationsFromCache()
        }

        val recitations = recitationsDatabaseMapper.mapFrom(recitationModels).sortedBy { it.name }
        val currentRecitation = recitations
                .firstOrNull { it.id == appPreferences.getCurrentRecitationId() }
                ?: recitations.first()
        return RecitationsList(recitations, currentRecitation)
    }

    private suspend fun getRecitationsFromCache(): List<RecitationModel> {
        val recitationModels = recitationsDao.getRecitations()
        //if cache is empty, fetch from server
        return if (recitationModels.isEmpty()) {
            downloadRecitationsFromServer()
        } else {
            recitationModels
        }
    }

    internal suspend fun downloadRecitationsFromServer(languageCode: String? = null): List<RecitationModel> {
        val rectationsResponse = quranAcademyApi.getRecitations(languageCode)
        val recitations = recitationsResponseMapper.mapFrom(rectationsResponse.recitations)
        appPreferences.setRecitationsListUpdateTime(DateTime.now())
        recitationsDao.saveRecitations(recitations)
        return recitations
    }

}