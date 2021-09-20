package org.quranacademy.quran.data.network

import org.quranacademy.quran.data.network.requests.FeedbackRequest
import org.quranacademy.quran.data.network.responses.*
import retrofit2.http.*

interface QuranAcademyApi {

    @GET("minimum-app-version/android")
    suspend fun getMinimumAppBuildVersion(): MinimumAppBuildVersionResponse

    @GET("languages")
    suspend fun getLanguages(): LanguagesListResponse

    @GET("quran/suras")
    suspend fun getSurahs(@Header("Locale") locale: String): SurahsListResponse

    @GET("quran/translations")
    suspend fun getAyahTranslationsList(
            @Header("Locale") locale: String?,
            @Query("format_version") formatVersion: Int = 2
    ): TranslationsListResponse

    @GET("quran/word-by-word-translations")
    suspend fun getWordByWordTranslationsList(
            @Header("Locale") locale: String?
    ): WordByWordranslationsListResponse

    @GET("quran/audio/recitations")
    suspend fun getRecitations(@Header("Locale") locale: String? = null): RecitationsListResponse

    @POST("feedback")
    suspend fun sendFeedback(@Body feedback: FeedbackRequest)

}