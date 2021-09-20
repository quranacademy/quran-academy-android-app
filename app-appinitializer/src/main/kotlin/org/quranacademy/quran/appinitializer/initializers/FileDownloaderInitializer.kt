package org.quranacademy.quran.appinitializer.initializers

import android.content.Context
import com.downloader.PRDownloader
import org.quranacademy.quran.appinitializer.AppInitializerElement
import javax.inject.Inject

class FileDownloaderInitializer @Inject constructor(

) : AppInitializerElement {

    override fun initialize(context: Context) {
        PRDownloader.initialize(context);
    }

}