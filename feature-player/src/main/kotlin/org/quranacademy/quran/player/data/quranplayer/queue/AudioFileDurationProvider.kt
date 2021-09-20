package org.quranacademy.quran.player.data.quranplayer.queue

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import javax.inject.Inject

class AudioFileDurationProvider @Inject constructor(
        private val context: Context
) {

    fun getAudioDuration(audioFile: File): Long {
        return if (audioFile.exists()) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, Uri.fromFile(audioFile))
            val durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr.toLong()
        } else {
            -1
        }
    }

}