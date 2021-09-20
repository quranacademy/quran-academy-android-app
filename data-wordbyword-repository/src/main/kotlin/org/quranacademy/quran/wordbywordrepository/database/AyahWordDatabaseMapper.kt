package org.quranacademy.quran.wordbywordrepository.database

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.models.AyahWordModel
import org.quranacademy.quran.data.database.models.AyahWordTranslationModel
import org.quranacademy.quran.domain.models.AyahWord
import org.quranacademy.quran.extensions.normalizeHarfsInText
import javax.inject.Inject

class AyahWordDatabaseMapper @Inject constructor() {

    fun map(words: List<AyahWordModel>, wordsTranslations: List<AyahWordTranslationModel>?): List<AyahWord> {
        return words.mapIndexed { index, ayahWordModel ->
            val isSajdaWord = QuranConstants.SAJDA_PLACES.filter { sajdaPlace ->
                sajdaPlace.surahNumber == ayahWordModel.surahNumber
                        && sajdaPlace.ayahNumber == ayahWordModel.ayahNumber
                        && sajdaPlace.wordIndex == ayahWordModel.position
            }.isNotEmpty()
            return@mapIndexed AyahWord(
                    position = ayahWordModel.position,
                    arabicText = ayahWordModel.arabicText.normalizeHarfsInText(),
                    arabicTextTajweed = ayahWordModel.arabicTextTajweed.normalizeHarfsInText(),
                    translationText = wordsTranslations?.get(index)?.text,
                    isSajdaWord = isSajdaWord
            )
        }
    }

}