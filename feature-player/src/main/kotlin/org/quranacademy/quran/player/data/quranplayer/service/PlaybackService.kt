package org.quranacademy.quran.player.data.quranplayer.service

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.lifecycle.ServiceLifecycleOwner
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.domain.models.PlaybackOptions
import org.quranacademy.quran.domain.models.PlaybackRequest
import org.quranacademy.quran.domain.models.PlayerState
import org.quranacademy.quran.player.data.quranplayer.PlaybackData
import org.quranacademy.quran.player.data.quranplayer.notification.MediaSessionHolder
import org.quranacademy.quran.player.data.quranplayer.notification.PlaybackNotificationFactory
import org.quranacademy.quran.player.data.quranplayer.notification.PlaybackServiceMediaSession
import org.quranacademy.quran.player.data.quranplayer.playbackmanager.QuranAudioPlaybackManager
import org.quranacademy.quran.player.di.playerservice.PlayerServiceModule
import toothpick.Scope
import toothpick.Toothpick

class PlaybackService : Service() {

    companion object {
        private val NOTIFICATION_ID = 666

        val ACTION_RESEND_STATE = "ACTION_RESEND_STATE"

        val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        val ACTION_PLAY = "ACTION_PLAY"
        val ACTION_PAUSE = "ACTION_PAUSE"
        val ACTION_RESUME = "ACTION_RESUME"
        val ACTION_STOP = "ACTION_STOP"
        val ACTION_STOP_WITH_ERROR = "ACTION_STOP_WITH_ERROR"

        val ACTION_PREV = "ACTION_PREV"
        val ACTION_NEXT = "ACTION_NEXT"

        val ACTION_SEEK = "ACTION_SEEK"
        val ACTION_CHANGE_PLAYBACK_OPTIONS = "ACTION_CHANGE_PLAYBACK_OPTIONS"
        val EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE"

        val EXTRA_PLAYBACK_REQUEST = "EXTRA_PLAYBACK_REQUEST"
        val EXTRA_CHANGE_PLAYBACK_OPTIONS = "CHANGE_PLAYBACK_OPTIONS"
        val EXTRA_PLAYBACK_POSITION = "EXTRA_PLAYBACK_POSITION"
    }

    private lateinit var scope: Scope

    //lifecycle
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private val serviceLifecycleOwner = ServiceLifecycleOwner()

    //notification
    private val playbackNotificationFactory by lazy { scope.get<PlaybackNotificationFactory>() }
    private val mediaSessionHolder by lazy { scope.get<MediaSessionHolder>() }

    private val playbackData = getGlobal<PlaybackData>()
    private val quranAudioPlaybackManager by lazy { scope.get<QuranAudioPlaybackManager>() }

    private val notificationManager = getGlobal<Context>().getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override fun onCreate() {
        super.onCreate()

        scope = Toothpick.openScopes(DI.APP_SCOPE, "PlaybackService")
        scope.installModules(PlayerServiceModule(coroutineScope))

        // Ensure the ordering of these two does not change
        serviceLifecycleOwner.registerLifecycleObserver(PlaybackServiceMediaSession(mediaSessionHolder))
        serviceLifecycleOwner.notifyLifecycleStateOnCreate()

        //The player should be started within five
        //seconds after the service is created
        //https://stackoverflow.com/a/45047542
        showNotification()

        coroutineScope.launch {
            quranAudioPlaybackManager.stateChangeUpdates()
                    .collect { state ->
                        Log.d("HQA", "playbackState: $state")
                        playbackData.currentAudio = quranAudioPlaybackManager.getNowPlaying()
                        playbackData.playbackState = state
                        when (state) {
                            PlayerState.PLAYING -> {
                                mediaSessionHolder.reportAyahChanged(playbackData.currentAudio!!)
                                mediaSessionHolder.reportPlaybackStateChanged(state, null)
                                showNotification()
                            }
                            PlayerState.PAUSED -> showNotification()
                            PlayerState.IDLE -> stopForeground(true)
                        }
                    }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val externalStoragePermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

        if (externalStoragePermissionGranted) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val action = intent.action
        if (action == null) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        when (intent.action) {
            ACTION_PLAY_PAUSE -> quranAudioPlaybackManager.playPause()
            ACTION_PLAY -> onActionPlay(intent)
            ACTION_RESUME -> quranAudioPlaybackManager.resume()
            ACTION_PAUSE -> quranAudioPlaybackManager.pause()
            ACTION_STOP -> quranAudioPlaybackManager.stop()
            ACTION_STOP_WITH_ERROR -> quranAudioPlaybackManager.onError(intent.getStringExtra(EXTRA_ERROR_MESSAGE))
            ACTION_PREV -> quranAudioPlaybackManager.prevAyah()
            ACTION_NEXT -> quranAudioPlaybackManager.nextAyah()
            ACTION_SEEK -> onActionSeek(intent)
            ACTION_CHANGE_PLAYBACK_OPTIONS -> onActionChangePlaybackOptions(intent)
            Intent.ACTION_MEDIA_BUTTON -> onActionMediaButton(intent)
            else -> {
                stopSelf(startId)
                return START_NOT_STICKY
            }
        }
        return START_NOT_STICKY
    }

    private fun onActionMediaButton(intent: Intent) {
        val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN) {
            when (keyEvent.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY -> quranAudioPlaybackManager.resume()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> quranAudioPlaybackManager.pause()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> quranAudioPlaybackManager.prevAyah()
                KeyEvent.KEYCODE_MEDIA_NEXT -> quranAudioPlaybackManager.nextAyah()
                KeyEvent.KEYCODE_MEDIA_STOP -> quranAudioPlaybackManager.stop()
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> quranAudioPlaybackManager.playPause()
            }
        }
    }

    private fun onActionPlay(intent: Intent) {
        val request = intent.getSerializableExtra(EXTRA_PLAYBACK_REQUEST) as PlaybackRequest
        quranAudioPlaybackManager.play(request)
    }

    private fun onActionSeek(intent: Intent) {
        if (intent.hasExtra(EXTRA_PLAYBACK_POSITION)) {
            val position = intent.getLongExtra(EXTRA_PLAYBACK_POSITION, 0)
            val ayahAudio = playbackData.currentAudio
            if (ayahAudio != null) {
                if (ayahAudio.duration > 0) {
                    playbackData.playbackProgress = position
                    quranAudioPlaybackManager.seekTo(position)
                }
            }
        }
    }

    private fun onActionChangePlaybackOptions(intent: Intent) {
        val options = intent.getSerializableExtra(EXTRA_CHANGE_PLAYBACK_OPTIONS) as PlaybackOptions
        quranAudioPlaybackManager.changePlaybackOptions(options)
    }

    private fun showNotification() {
        mediaSessionHolder.mediaSession?.let { mediaSession ->
            val notification = playbackNotificationFactory.create(
                    context = this,
                    state = playbackData.playbackState,
                    mediaSession = mediaSession
            )

            if (isServiceRunningInForeground()) {
                notificationManager.notify(NOTIFICATION_ID, notification)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        parentJob.cancel()
        serviceLifecycleOwner.notifyLifecycleStateOnDestroy()
        Toothpick.closeScope(scope)
    }

    private fun isServiceRunningInForeground(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private inner class ResendStateReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            mediaSessionHolder.reportPlaybackStateChanged(playbackData.playbackState, null)
        }

    }

}