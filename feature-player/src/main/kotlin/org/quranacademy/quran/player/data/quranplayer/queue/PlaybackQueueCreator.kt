package org.quranacademy.quran.player.data.quranplayer.queue

import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.data.database.SurahNameProvider
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.domain.models.VerseRange
import org.quranacademy.quran.recitationsrepository.AudioPathProvider
import java.io.File
import javax.inject.Inject

class PlaybackQueueCreator @Inject constructor(
        private val surahNameProvider: SurahNameProvider,
        private val audioPathProvider: AudioPathProvider,
        private val audioFileDurationProvider: AudioFileDurationProvider
) {

    fun createQueue(
            verseRange: VerseRange,
            recitation: Recitation
    ): List<AyahAudio> {
        //вычисляем промежуток (список) аятов для воспроизведения
        val ayahsQueue = (verseRange.startSurah..verseRange.endingSurah).map { surahNumber ->
            val surahPosition = surahNumber - 1
            val surahAyahsNumber = QuranConstants.SURAH_AYAHS_NUMBER[surahPosition]
            return@map (1..surahAyahsNumber).map { ayahNumber -> AyahId(surahNumber, ayahNumber) }
        }.toMutableList()
        //обрезаем начальную суру до нужного аята
        val firstSurahAyahs = ayahsQueue.first()
        ayahsQueue[0] = firstSurahAyahs.drop(verseRange.startAyah - 1)
        //обрезаем конечную суру до нужного аята
        val lastSurahAyahs = ayahsQueue.last()
        ayahsQueue[ayahsQueue.lastIndex] = lastSurahAyahs.dropLast(lastSurahAyahs.size - verseRange.endingAyah)

        return ayahsQueue.flatten()
                .map { ayah -> createAudio(ayah.surahNumber, ayah.ayahNumber, recitation) }
    }

    private fun createAudio(
            surahNumber: Int,
            ayahNumber: Int,
            recitation: Recitation
    ): AyahAudio {
        val audioFile = File(audioPathProvider.getAyahAudioPath(surahNumber, ayahNumber, recitation))
        return AyahAudio(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahNameProvider.getSurahName(surahNumber),
                audioFile = audioFile,
                duration = AyahAudio.UNKNOWN_DURATION,
                recitation = recitation
        )
    }

}