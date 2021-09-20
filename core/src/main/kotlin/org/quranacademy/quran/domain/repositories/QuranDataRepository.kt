package org.quranacademy.quran.domain.repositories

interface QuranDataRepository {

    suspend fun prepareQuranArabicTexts()

    suspend fun isInitialSetupCompleted(): Boolean

    suspend fun setIsInitialSetupCompleted(isCompleted: Boolean)

}