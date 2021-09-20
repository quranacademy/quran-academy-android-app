package org.quranacademy.quran.surahsrepository

import org.quranacademy.quran.data.database.models.SurahModel
import org.quranacademy.quran.data.network.responses.SurahResponse
import javax.inject.Inject

class SurahResponseDatabaseMapper @Inject constructor() {

    fun map(models: List<SurahResponse>) = models.map { map(it) }

    fun map(entity: SurahResponse): SurahModel {
        return SurahModel(
                id = entity.id,
                surahNumber = entity.surahNumber,
                bismillahPre = entity.bismillahPre,
                //revelationPlace = entity.revelationPlace,
                //revelationOrder = entity.revelationOrder,
                juzNumber = entity.juzNumber,
                hizbNumber = entity.hizbNumber,
                rubNumber = entity.rubNumber,
                ayahsCount = entity.ayahsCount,
                arabicName = entity.name.arabicName
        )

    }

}