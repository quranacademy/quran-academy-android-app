package org.quranacademy.quran.audiomanager.ui.recitersslist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recitation_download.view.*
import org.quranacademy.quran.audiomanager.R
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.presentation.extensions.bytes2String
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo

class RecitationsAdapter(
        private val downloadClickListener: (Recitation) -> Unit,
        private val removeClickListener: (Recitation) -> Unit,
        private val openRecitationDetails: (Recitation) -> Unit
) : RecyclerView.Adapter<RecitationsAdapter.Holder>() {

    private var recitationsAudioInfo: List<RecitationAudioInfo> = emptyList()

    override fun getItemCount() = recitationsAudioInfo.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = parent.inflate(R.layout.item_recitation_download)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val recitationAudioInfo = recitationsAudioInfo[position]
        val recitation = recitationAudioInfo.recitation
        val itemView = holder.itemView

        itemView.recitationTitle.text = recitation.name
        itemView.recitationDescription.text = if (recitationAudioInfo.downloadedAudioSizeBytes > 0) {
            itemView.context.getString(
                    R.string.recitation_audio_info,
                    recitationAudioInfo.downloadedSurahsCount,
                    recitationAudioInfo.downloadedAudioSizeBytes.bytes2String()
            )
        } else {
            itemView.context.getString(R.string.audio_not_downloaded)
        }

        itemView.downloadRecitationButton.visible(!recitationAudioInfo.isFullyDownloaded)
        itemView.removeRecitationButton.visible(recitationAudioInfo.downloadedAudioSizeBytes > 0)

        itemView.setOnClickListener { openRecitationDetails(recitation) }
        itemView.downloadRecitationButton.setOnClickListener { downloadClickListener(recitation) }
        itemView.removeRecitationButton.setOnClickListener { removeClickListener(recitation) }
    }

    fun setData(recitationsAudioInfo: List<RecitationAudioInfo>) {
        val diffUtilCallback = RecitationsInfoDiffUtils(this.recitationsAudioInfo, recitationsAudioInfo)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        this.recitationsAudioInfo = recitationsAudioInfo
        diffResult.dispatchUpdatesTo(this)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}