package org.quranacademy.quran.radio.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.radio.data.radio.RadioState
import org.quranacademy.quran.radio.data.service.RadioServiceIntentFactory
import org.quranacademy.quran.radio.presentation.RadioActivity
import javax.inject.Inject

class RadioNotificationFactory @Inject constructor(
        private val intentFactory: RadioServiceIntentFactory
) {

    companion object {
        private const val CHANNEL_ID = "HQARadio"

        // pending notification requestPermissions codes
        private const val REQUEST_CODE_MAIN = 0
        private const val REQUEST_CODE_PAUSE = 2
        private const val REQUEST_CODE_STOP = 4
        private const val REQUEST_CODE_RESUME = 5

    }

    fun create(
            context: Context,
            state: RadioState,
            mediaSession: MediaSessionCompat
    ): Notification {
        ensureChannelExists(context)

        val isPlaying = state == RadioState.PLAYING
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata

        val contentIntent = createContentIntent(context)
        val style = createNotificationStyle(mediaSession)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setStyle(style)
                .setShowWhen(false)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(if (isPlaying) R.drawable.ic_pause_gray_24dp else R.drawable.ic_play_24dp)
                .setLargeIcon(generateNotificationIcon(context))

        addActionButtons(context, builder, isPlaying)

        return builder.build()
    }

    /**
     * Generate the notification icon
     * This might return null if the icon fails to initialize. This method should
     * be called from a separate background thread (other than the one the service
     * is running on).
     *
     * @return a bitmap of the notification icon
     */
    private fun generateNotificationIcon(context: Context): Bitmap? {
        val drawable = context.resources.getDrawable(R.drawable.ic_launcher)
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun createContentIntent(context: Context): PendingIntent {
        //ToDo: открывать нужную активити и открывать плееер
        val contentIntent = Intent(context, RadioActivity::class.java)
        return PendingIntent.getActivity(context, REQUEST_CODE_MAIN, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationStyle(mediaSession: MediaSessionCompat): NotificationCompat.Style {
        return androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1)
    }

    private fun addActionButtons(
            context: Context,
            builder: NotificationCompat.Builder,
            isPlaying: Boolean
    ) {

        addPlayPauseButton(context, builder, isPlaying)

        val stopActionIntent = PendingIntent.getService(
                context,
                REQUEST_CODE_STOP,
                intentFactory.intentStop(),
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.addAction(R.drawable.ic_stop_white_24dp, context.getText(R.string.stop), stopActionIntent)
    }

    private fun addPlayPauseButton(
            context: Context,
            builder: NotificationCompat.Builder,
            isPlaying: Boolean
    ) {
        if (isPlaying) {
            val pauseActionIntent = PendingIntent.getService(
                    context,
                    REQUEST_CODE_PAUSE,
                    intentFactory.intentPause(),
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.addAction(R.drawable.ic_pause_gray_24dp, context.getText(R.string.pause), pauseActionIntent)
        } else {
            val playActionIntent = PendingIntent.getService(
                    context,
                    REQUEST_CODE_RESUME,
                    intentFactory.intentPlay(),
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder.addAction(R.drawable.ic_play_24dp, context.getText(R.string.play), playActionIntent)
        }
    }

    private fun ensureChannelExists(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ensureChannelExistsV26(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ensureChannelExistsV26(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        if (notificationManager != null) {
            ensureChannelExists(context, notificationManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ensureChannelExists(
            context: Context,
            notificationManager: NotificationManager
    ) {
        if (!hasChannels(notificationManager)) {
            val channel = createChannel(context)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context): NotificationChannel {
        val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.quran_academy_radio),
                NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.setShowBadge(true)
        channel.enableVibration(false)
        channel.setSound(null, null)
        return channel
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasChannels(notificationManager: NotificationManager): Boolean {
        val channels = notificationManager.notificationChannels
        return channels.firstOrNull { it.id == CHANNEL_ID } != null
    }

}