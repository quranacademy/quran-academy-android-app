package org.quranacademy.quran.radio.data.service

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.KeyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.lifecycle.ServiceLifecycleOwner
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.radio.data.di.RadioServiceModule
import org.quranacademy.quran.radio.data.manager.RadioData
import org.quranacademy.quran.radio.data.manager.RadioManager
import org.quranacademy.quran.radio.data.notification.RadioMediaSessionHolder
import org.quranacademy.quran.radio.data.notification.RadioNotificationFactory
import org.quranacademy.quran.radio.data.notification.RadioServiceMediaSession
import org.quranacademy.quran.radio.data.radio.RadioState
import toothpick.Scope
import toothpick.Toothpick

class RadioService : Service() {

    companion object {
        private val NOTIFICATION_ID = 753

        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private lateinit var scope: Scope

    //lifecycle
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private val serviceLifecycleOwner = ServiceLifecycleOwner()

    //notification
    private val playbackNotificationFactory by lazy { scope.get<RadioNotificationFactory>() }
    private val mediaSessionHolder by lazy { scope.get<RadioMediaSessionHolder>() }

    private val radioManager by lazy { scope.get<RadioManager>() }
    private val radioData by lazy { scope.get<RadioData>() }

    private val notificationManager = getGlobal<Context>().getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    override fun onCreate() {
        super.onCreate()

        scope = Toothpick.openScopes(DI.APP_SCOPE, "RadioService")
        scope.installModules(RadioServiceModule(coroutineScope))

        // Ensure the ordering of these two does not change
        serviceLifecycleOwner.registerLifecycleObserver(RadioServiceMediaSession(mediaSessionHolder))
        serviceLifecycleOwner.notifyLifecycleStateOnCreate()

        coroutineScope.launch {
            radioData.radioStateUpdates()
                    .collect { state ->
                        when (state) {
                            RadioState.PLAYING -> {
                                mediaSessionHolder.reportPlaybackStateChanged(state, null)
                                showNotification()
                            }
                            RadioState.PAUSED -> showNotification()
                            RadioState.IDLE -> stopForeground(true)
                        }
                    }
        }

        coroutineScope.launch {
            radioManager.metadataChanged().collect {
                mediaSessionHolder.reportTrackChanged(it)
                showNotification()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            ACTION_PLAY -> radioManager.play()
            ACTION_PAUSE -> radioManager.pause()
            ACTION_STOP -> radioManager.stop()
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
                KeyEvent.KEYCODE_MEDIA_PLAY -> radioManager.play()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> radioManager.pause()
                KeyEvent.KEYCODE_MEDIA_STOP -> radioManager.stop()
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> radioManager.playPause()
            }
        }
    }

    private fun showNotification() {
        mediaSessionHolder.mediaSession?.let { mediaSession ->
            val notification = playbackNotificationFactory.create(
                    context = this,
                    state = radioData.playbackState,
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
            mediaSessionHolder.reportPlaybackStateChanged(radioData.playbackState, null)
        }

    }

}