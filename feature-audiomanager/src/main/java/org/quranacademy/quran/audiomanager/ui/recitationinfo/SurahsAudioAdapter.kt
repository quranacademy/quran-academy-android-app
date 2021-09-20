package org.quranacademy.quran.audiomanager.ui.recitationinfo

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recitation_surah.view.*
import org.quranacademy.quran.QuranConstants
import org.quranacademy.quran.audiomanager.R
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.SurahAudioInfo

class SurahsAudioAdapter(
        private val downloadClickListener: (SurahAudioInfo) -> Unit,
        private val removeClickListener: (SurahAudioInfo) -> Unit
) : RecyclerView.Adapter<SurahsAudioAdapter.Holder>() {

    private var surahsAudioInfo: List<SurahAudioInfo> = emptyList()

    override fun getItemCount() = surahsAudioInfo.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_recitation_surah)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val surahAudioInfo = surahsAudioInfo[position]
        val itemView = holder.itemView

        val surahNumber = surahAudioInfo.surahNumber
        val surahName = surahAudioInfo.surahName
        itemView.surahName.text = "$surahNumber. $surahName"
        itemView.recitationDescription.text = if (surahAudioInfo.downloadedAudioSizeBytes > 0) {
            itemView.context.getString(
                    R.string.surah_audio_info,
                    surahAudioInfo.downloadedAyahsNumber,
                    QuranConstants.SURAH_AYAHS_NUMBER[surahNumber - 1],
                    surahAudioInfo.downloadedAudioSizeBytes.bytes2String()
            )
        } else {
            itemView.context.getString(R.string.audio_not_downloaded)
        }

        itemView.downloadSurahButton.visible(!surahAudioInfo.isFullyDownloaded)
        itemView.removeSurahButton.visible(surahAudioInfo.downloadedAudioSizeBytes > 0)

        itemView.downloadSurahButton.setOnClickListener { downloadClickListener(surahAudioInfo) }
        itemView.removeSurahButton.setOnClickListener { removeClickListener(surahAudioInfo) }
    }

    fun setData(surahsAudioInfo: List<SurahAudioInfo>) {
        val diffUtilCallback = SurahsAudioInfoDiffUtils(this.surahsAudioInfo, surahsAudioInfo)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        this.surahsAudioInfo = surahsAudioInfo
        diffResult.dispatchUpdatesTo(this)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
