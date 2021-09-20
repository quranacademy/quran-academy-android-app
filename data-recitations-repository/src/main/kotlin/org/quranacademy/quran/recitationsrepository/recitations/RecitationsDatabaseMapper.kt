package org.quranacademy.quran.recitationsrepository.recitations

import org.quranacademy.quran.data.database.models.RecitationModel
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.recitationsrepository.TimecodesPathProvider
import javax.inject.Inject

class RecitationsDatabaseMapper @Inject constructor(
        private val timecodesPathProvider: TimecodesPathProvider
) {

    fun mapFrom(models: List<RecitationModel>): List<Recitation> {
        return models.map { model ->
            return@map mapFrom(model)
        }
    }

    fun mapFrom(model: RecitationModel): Recitation {
        val isTimecodesDownloaded = model.timecodesFileUrl != null &&
                timecodesPathProvider.getTimecodesDbFile(model.id).exists()
        return Recitation(
                id = model.id,
                name = model.name,
                urlDownloadTemplate = model.audioUrlTemplate,
                timecodesFileUrl = model.timecodesFileUrl,
                isTimecodesDownloaded = isTimecodesDownloaded
        )
    }


}