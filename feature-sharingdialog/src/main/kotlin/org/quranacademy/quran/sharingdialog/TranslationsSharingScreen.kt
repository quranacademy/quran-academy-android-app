package org.quranacademy.quran.sharingdialog

import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Translation
import java.io.Serializable

class TranslationsSharingScreen(
        val translations: List<Translation>,
        val ayahs: List<AyahId>,
        val type: SharingType
) : Screen, Serializable {

    class Result(
            val selectedTranslations: List<Translation>,
            val ayahs: List<AyahId>,
            val type: SharingType,
            val copyQuranArabicText: Boolean
    ) : ScreenResult

}