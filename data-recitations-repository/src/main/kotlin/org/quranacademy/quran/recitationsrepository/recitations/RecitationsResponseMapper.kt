package org.quranacademy.quran.recitationsrepository.recitations

import org.quranacademy.quran.data.database.models.RecitationModel
import org.quranacademy.quran.data.network.responses.RecitationResponse
import javax.inject.Inject

class RecitationsResponseMapper @Inject constructor() {

    fun mapFrom(models: List<RecitationResponse>): List<RecitationModel> {
        return models.map { model ->
            return@map RecitationModel(
                    id = model.id,
                    name = model.name,
                    audioUrlTemplate = model.audioUrlTemplate,
                    timecodesFile = model.timecodesFile
            )
        }
    }

}