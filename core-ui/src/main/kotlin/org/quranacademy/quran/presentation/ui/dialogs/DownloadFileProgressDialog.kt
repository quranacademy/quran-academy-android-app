package org.quranacademy.quran.presentation.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.dialog_progress.view.*
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage

open class DownloadFileProgressDialog(
        private val context: Context
) {

    private val dialog = MaterialDialog(context)
    private val contentView: View = context.inflate(R.layout.dialog_progress)
    private val isCurrentLanguageRtl = getCurrentAppLanguage().isRtl

    init {
        dialog.title(R.string.download_dialog_title)
                .message(R.string.download_dialog_title)
                .customView(view = contentView)
                .cancelable(false)
                .cancelOnTouchOutside(false)
    }

    fun setTitle(title: String): DownloadFileProgressDialog {
        dialog.title(text = title)
        return this
    }

    fun cancelButton(onCancelListener: () -> Unit): DownloadFileProgressDialog {
        dialog.negativeButton(R.string.btn_cancel_title) { onCancelListener() }
        return this
    }

    fun indeterminate(isIndeterminate: Boolean): DownloadFileProgressDialog {
        contentView.progressBar.isIndeterminate = isIndeterminate
        return this
    }

    fun show(): DownloadFileProgressDialog {
        dialog.show()
        return this
    }

    fun dismiss() {
        dialog.dismiss()
    }

    @SuppressLint("SetTextI18n")
    fun updateDownloadProgress(progress: FileDownloadInfo, updateMessage: Boolean = true) {
        val downloadedSize = progress.downloadedSize
        val totalSize = progress.totalSize
        contentView.progressBar.isIndeterminate = false
        val percents = ((downloadedSize.toFloat() / totalSize.toFloat()) * 100f).toInt()
        contentView.progressPercentsLabel.visible(true)
        contentView.progressPercentsLabel.text = "$percents%"
        contentView.progressBar.max = totalSize.toInt()
        contentView.progressBar.progress = downloadedSize.toInt()

        if (updateMessage) {
            val message = context.getString(
                    R.string.download_progress_dialog_message,
                    downloadedSize.bytes2String(isCurrentLanguageRtl),
                    totalSize.bytes2String(isCurrentLanguageRtl)
            )
            updateMessage(message)
        }
    }

    fun updateMessage(message: String): DownloadFileProgressDialog {
        dialog.message(text = message)
        return this
    }

}