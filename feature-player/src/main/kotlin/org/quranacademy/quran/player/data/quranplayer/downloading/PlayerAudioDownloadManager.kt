package org.quranacademy.quran.player.data.quranplayer.downloading

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.quranacademy.quran.data.network.networkobserver.ConnectivityPredicate
import org.quranacademy.quran.data.network.networkobserver.ReactiveNetwork
import org.quranacademy.quran.domain.models.AyahAudio
import org.quranacademy.quran.player.data.quranplayer.PlaybackException
import org.quranacademy.quran.player.di.playerservice.PlayerServiceScope
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadException
import org.quranacademy.quran.recitationsrepository.downloading.AudioDownloadTask
import org.quranacademy.quran.recitationsrepository.downloading.QuranAudioDownloader
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("MissingPermission")
class PlayerAudioDownloadManager @Inject constructor(
        @PlayerServiceScope
        private val serviceScope: CoroutineScope,
        private val quranAudioDownloader: QuranAudioDownloader,
        private val onAyahDownloadedListener: Listener,
        context: Context
) {
    private var downloadQueue: MutableList<AyahAudio>? = null
    private var currentDownloadableAudio: AyahAudio? = null
    private var currentDownloadTask: AudioDownloadTask? = null
    var currentError: AudioDownloadException? = null

    init {
        try {
            serviceScope.launch {
                ReactiveNetwork
                        .observeNetworkConnectivity(context)
                        .flowOn(Dispatchers.IO)
                        .filter { ConnectivityPredicate.hasState(it, NetworkInfo.State.CONNECTED) }
                        .filter { ConnectivityPredicate.hasType(it, ConnectivityManager.TYPE_WIFI) }
                        .collect {
                            serviceScope.launch(context = Dispatchers.Main) { onConnectionRestored() }
                        }
            }
        } catch (error: Exception) {
            Timber.e(error)
        }
    }

    suspend fun downloadAudio(ayahs: List<AyahAudio>, currentPlaybackPosition: Int) {
        this.downloadQueue = if (currentPlaybackPosition > 0) {
            //move current and next ayahs to the start of download queue
            //to avoid waiting for downloading previous ayahs
            val first = ayahs.subList(0, currentPlaybackPosition)
            val second = ayahs.subList(currentPlaybackPosition, ayahs.size - 1)
            second + first
        } else {
            ayahs
        }.filter { ayah -> !ayah.audioFile.exists() }.toMutableList()

        startDownloading()
    }

    fun isAudioDownloading(ayah: AyahAudio): Boolean {
        val isErrorOccurred = currentError != null
        return !isErrorOccurred && (ayah == currentDownloadableAudio || downloadQueue?.contains(ayah) ?: false)
    }

    fun clearQueue() = downloadQueue?.clear()

    private suspend fun startDownloading() {
        val queue = downloadQueue
        RuntimeException().printStackTrace()
        if (queue != null && queue.isNotEmpty()) {
            try {
                val ayahAudio = queue.first()
                currentDownloadableAudio = ayahAudio
                onAyahDownloadedListener?.onAyahDownloadStarted(ayahAudio)
                currentDownloadTask = quranAudioDownloader.downloadAudio(
                        surahNumber = ayahAudio.surahNumber,
                        ayahNumber = ayahAudio.ayahNumber,
                        recitation = ayahAudio.recitation
                )
                currentDownloadTask?.start()
                onDownloadingFinished()
            } catch (error: AudioDownloadException) {
                currentDownloadableAudio?.let { ayahAudio ->
                    onAyahDownloadError(ayahAudio, error)
                }
            }
        }
    }

    private suspend fun onDownloadingFinished() {
        currentDownloadableAudio?.let { onAyahDownloadedListener.onAyahDownloaded(it) }
        downloadQueue?.let { downloadingQueue ->
            if (downloadingQueue.isNotEmpty()) {
                //remove first item, because it downloaded yet
                downloadingQueue.removeAt(0)
            }
        }
        startDownloading()
    }

    private fun onAyahDownloadError(ayahAudio: AyahAudio, error: AudioDownloadException) {
        val errorWrapper = PlaybackException.Downloading(ayahAudio, error)
        currentError = error
        onAyahDownloadedListener?.onDownloadingError(ayahAudio, errorWrapper)
    }

    private suspend fun onConnectionRestored() {
        if (currentError is AudioDownloadException.NoNetwork) {
            currentError = null
            startDownloading()
        }
    }

    interface Listener {

        fun onAyahDownloadStarted(ayahAudio: AyahAudio)

        fun onAyahDownloaded(ayahAudio: AyahAudio)

        fun onDownloadingError(ayahAudio: AyahAudio, error: PlaybackException)

    }

}