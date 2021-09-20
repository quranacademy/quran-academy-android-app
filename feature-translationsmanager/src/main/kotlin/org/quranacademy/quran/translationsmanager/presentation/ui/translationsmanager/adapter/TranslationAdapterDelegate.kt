package org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_translation.view.*
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.setDebounceOnClickLister
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.domain.translations.TranslationUIModel

class TranslationAdapterDelegate(
        private val onDownloadTranslationClicked: (TranslationUIModel) -> Unit,
        private val onCancelDownloadingClicked: (TranslationUIModel) -> Unit,
        private val updateClickListener: (TranslationUIModel) -> Unit,
        private val removeClickListener: (TranslationUIModel) -> Unit,
        private val onEnableTranslationClicked: (TranslationUIModel, Boolean) -> Unit
) : AdapterDelegate<MutableList<Any>>() {

    private val downloadingProgresses = mutableMapOf<String, FileDownloadInfo>()
    private val progressListeners = mutableMapOf<String, ProgressListener>()

    override fun isForViewType(items: MutableList<Any>, position: Int) =
            items[position] is TranslationUIModel

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_translation))

    override fun onBindViewHolder(
            items: MutableList<Any>,
            position: Int,
            viewHolder: RecyclerView.ViewHolder,
            payloads: MutableList<Any>
    ) = (viewHolder as ViewHolder).bind(items[position] as TranslationUIModel)

    fun updateDownloadProgress(translation: TranslationUIModel, downloadInfo: FileDownloadInfo?) {
        if (downloadInfo != null) {
            downloadingProgresses[translation.code] = downloadInfo
            progressListeners[translation.code]?.update(downloadInfo)
        } else {
            downloadingProgresses.remove(translation.code)
        }
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var translation: TranslationUIModel

        @SuppressLint("SetTextI18n")
        fun bind(translation: TranslationUIModel) {
            this.translation = translation

            itemView.enableTranslation.isChecked = translation.isEnabled
            itemView.translationTitle.text = translation.name
            itemView.translationInfo.text = "${translation.languageName} - ${translation.fileSize.bytes2String()}"

            itemView.enableTranslation.isEnabled = translation.isDownloaded
            itemView.translationTitle.isEnabled = translation.isDownloaded
            itemView.translationInfo.isEnabled = translation.isDownloaded

            getProgressListener()?.let { progressListener ->
                if (progressListener.translationCode != translation.code) {
                    progressListener.cancel()
                    itemView.tag = null
                }
            }

            if (translation.isDownloaded) {
                itemView.downloadTranslationButton.visible(false)
                itemView.downloadProgressContainer.visible(false)
                itemView.removeTranslationButton.setImageResource(R.drawable.ic_remove_black_24dp)
                itemView.removeTranslationButton.visibility = View.VISIBLE
                itemView.removeTranslationButton.setOnClickListener {
                    removeClickListener(translation)
                }
                itemView.setDebounceOnClickLister {
                    val isTranslationEnabled = !translation.isEnabled
                    itemView.enableTranslation.isChecked = isTranslationEnabled
                    onEnableTranslationClicked(translation, isTranslationEnabled)
                }

                if (translation.isUpdateAvailable) {
                    itemView.updateButton.visible(true)
                    itemView.updateButton.setOnClickListener {
                        updateClickListener(translation)
                    }
                } else {
                    itemView.updateButton.visible(false)
                }
            } else {
                itemView.removeTranslationButton.visible(false)
                itemView.updateButton.visible(false)
                itemView.downloadProgressbar.max = translation.fileSize.toInt()

                val progress = downloadingProgresses[translation.code]
                progress?.let {
                    itemView.downloadProgressbar.progress = it.downloadedSize.toInt()
                }

                if (translation.isDownloading) {
                    itemView.downloadTranslationButton.visible(false)
                    itemView.downloadProgressContainer.visible(true)
                    itemView.cancelDownloadingButton.setOnClickListener {
                        onCancelDownloadingClicked(translation)
                    }
                } else {
                    itemView.downloadTranslationButton.visible(true)
                    itemView.downloadProgressContainer.visible(false)
                    itemView.downloadTranslationButton.setOnClickListener {
                        onDownloadTranslationClicked(translation)
                    }

                    itemView.setOnClickListener {
                        onDownloadTranslationClicked(translation)
                    }
                }

                if (getProgressListener() == null) {
                    val progressListener = ProgressListener(translation.code) {
                        itemView.downloadProgressbar.progress = it.downloadedSize.toInt()
                    }
                    progressListeners[translation.code] = progressListener
                    itemView.tag = progressListener
                }
            }
        }

        private fun getProgressListener() = itemView.tag as ProgressListener?

    }

    private class ProgressListener(
            val translationCode: String,
            private val listener: (FileDownloadInfo) -> Unit
    ) {

        private var isCancelled = false

        fun update(info: FileDownloadInfo) {
            if (!isCancelled) {
                listener(info)
            }
        }

        fun cancel() {
            isCancelled = false
        }

    }

}