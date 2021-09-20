package org.quranacademy.quran.domain.repositories

import org.quranacademy.quran.domain.models.QuranPage

interface QuranPageRepository {

    suspend fun getPageInfo(pageNumber: Int): QuranPage

}