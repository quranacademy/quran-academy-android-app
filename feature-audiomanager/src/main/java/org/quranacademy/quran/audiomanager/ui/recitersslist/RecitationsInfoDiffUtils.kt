package org.quranacademy.quran.audiomanager.ui.recitersslist

import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.RecitationAudioInfo

class RecitationsInfoDiffUtils(
        oldList: List<RecitationAudioInfo>,
        newList: List<RecitationAudioInfo>
) : BaseDiffUtils<RecitationAudioInfo>(oldList, newList) {

    override fun areItemsTheSame(old: RecitationAudioInfo, new: RecitationAudioInfo): Boolean {
        return old.recitation.id == new.recitation.id
    }

    override fun areContentsTheSame(old: RecitationAudioInfo, new: RecitationAudioInfo): Boolean {
        return old.isFullyDownloaded == new.isFullyDownloaded &&
                old.downloadedAudioSizeBytes == new.downloadedAudioSizeBytes
    }

}