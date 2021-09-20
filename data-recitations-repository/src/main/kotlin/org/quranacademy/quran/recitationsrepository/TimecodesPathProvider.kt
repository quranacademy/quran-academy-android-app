package org.quranacademy.quran.recitationsrepository

import android.content.Context
import java.io.File
import javax.inject.Inject

class TimecodesPathProvider @Inject constructor(context: Context) {

    private val audioTimecodesFolder = File(context.filesDir, "audio_timecodes/")

    fun getTimecodesDbFile(recitationId: Long) = File(audioTimecodesFolder, "$recitationId.db")

}