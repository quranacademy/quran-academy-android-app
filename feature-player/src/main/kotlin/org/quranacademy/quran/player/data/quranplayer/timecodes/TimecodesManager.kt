package org.quranacademy.quran.player.data.quranplayer.timecodes

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.quranacademy.quran.data.database.models.AudioTimecodeModel
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.recitationsrepository.TimecodesPathProvider
import javax.inject.Inject

class TimecodesManager @Inject constructor(
        private val context: Context,
        private val timecodesPathProvider: TimecodesPathProvider,
        private val playbackData: PlaybackData
) {

    private var isEnabled = false
    private var databaseAdapter: TimecodesDatabaseAdapter? = null
    private var timecodes: Map<Int, List<AudioTimecodeModel>>? = null
    private var currentAyahTimecodes: List<AudioTimecodeModel>? = null
    private var currentSurah: Int? = null

    init {
        GlobalScope.launch {
            playbackData.currentAudioUpdates()
                    .collect {
                        if (isEnabled) {
                            onCurrentAyahChanged(it)
                        }
                    }
        }

        GlobalScope.launch {
            playbackData.currentAudioProgressUpdates()
                    .collect {
                        if (isEnabled) {
                            onAudioProgress(it)
                        }
                    }
        }
    }

    fun initTimecodes(recitation: Recitation) {
        val databasePath = timecodesPathProvider.getTimecodesDbFile(recitation.id)
        databaseAdapter = TimecodesDatabaseAdapter(context, databasePath.absolutePath)
    }

    fun enableHighlighting(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    fun onDestroy() {
        databaseAdapter?.close()
        databaseAdapter = null
        timecodes = null
        currentAyahTimecodes = null
        currentSurah = null
    }

    private suspend fun onCurrentAyahChanged(currentAyah: AyahAudio) {
        withContext(Dispatchers.IO) {
            if (currentSurah != currentAyah.surahNumber) {
                timecodes = databaseAdapter?.getSurahTimecodes(currentAyah.surahNumber)?.groupBy { it.ayahNumber }
                currentSurah = currentAyah.surahNumber
            }
            currentAyahTimecodes = timecodes!![currentAyah.ayahNumber]
        }
    }

    private fun onAudioProgress(currentAyahProgress: Long) {
        val currentWord = currentAyahTimecodes?.firstOrNull { it.startTime <= currentAyahProgress && currentAyahProgress <= it.endTime }
        playbackData.currentWordNumber = currentWord?.wordNumber
    }

}