package org.quranacademy.quran.ayahsrepository

import org.quranacademy.quran.data.database.models.AyahTranslationModel
import org.quranacademy.quran.domain.models.AyahTranslation
import org.quranacademy.quran.domain.models.Translation
import javax.inject.Inject

class AyahTranslationDatabaseMapper @Inject constructor(
        private val hqaTextFormatParser: HQATextFormatParser
) {

    fun map(model: AyahTranslationModel, translation: Translation): AyahTranslation {
        var simpleText: String? = null
        var textHQAFormat: AyahTranslation.HQATextFormat? = null

        if (model.isNewFormat) {
            try {
                textHQAFormat = hqaTextFormatParser.parse(model.text)
            } catch (error: Exception) {
                simpleText = "There is problem with translation text. Please contact support"
            }
        } else {
            simpleText = model.text
        }
        return AyahTranslation(
                translation = translation,
                simpleText = simpleText,
                textHQAFormat = textHQAFormat
        )
    }

}