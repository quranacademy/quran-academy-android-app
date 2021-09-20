package org.quranacademy.quran.recitationsrepository

import android.content.Context
import org.quranacademy.quran.data.PathProvider
import org.quranacademy.quran.domain.models.Recitation
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class AudioPathProvider @Inject constructor(
        context: Context,
        private val pathProvider: PathProvider
) {

    private val formatter: NumberFormat = DecimalFormat("000", DecimalFormatSymbols(Locale.ENGLISH))

    fun getRecitationFolder(recitationId: Long) = File(audioFolder(), recitationId.toString())

    fun getRecitationSurahFolder(recitationId: Long, surahNumber: Int) = File(audioFolder(), "$recitationId/$surahNumber")

    fun getAyahDownloadUrl(
            surahNumber: Int,
            ayahNumber: Int,
            recitation: Recitation
    ): String {
        return recitation.urlDownloadTemplate
                .replace("{sura}", formatter.format(surahNumber))
                .replace("{ayat}", formatter.format(ayahNumber))
    }

    fun getAyahAudioPath(
            surahNumber: Int,
            ayahNumber: Int,
            recitation: Recitation
    ): String {
        return File(audioFolder(), "${recitation.id}/$surahNumber/$ayahNumber.mp3").absolutePath
    }

    private fun audioFolder() = File(pathProvider.getAppFolder(), "audio/")

}