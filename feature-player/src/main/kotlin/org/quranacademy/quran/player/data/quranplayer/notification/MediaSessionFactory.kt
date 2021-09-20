package org.quranacademy.quran.player.data.quranplayer.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.session.MediaButtonReceiver
import org.quranacademy.quran.player.presentation.global.ImmediatelyFinishActivity
import javax.inject.Inject

class MediaSessionFactory @Inject constructor(
        private val context: Context,
        private val mediaSessionCallback: MediaSessionCompat.Callback
) {

    private val mediaButtonReceiver: ComponentName = ComponentName(context, MediaButtonReceiver::class.java)
    private val mediaButtonIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, MediaButtonReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun newMediaSession(): MediaSessionCompat {
        val mediaSession = MediaSessionCompat(context, TAG_MEDIA_SESSION, mediaButtonReceiver, mediaButtonIntent)
        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setSessionActivity(
                PendingIntent.getActivity(
                        context,
                        1,
                        Intent(context, ImmediatelyFinishActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        )
        mediaSession.isActive = true
        return mediaSession
    }

    companion object {

        private val TAG_MEDIA_SESSION = "Default"

    }

}