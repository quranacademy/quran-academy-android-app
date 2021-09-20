package org.quranacademy.quran.audiomanager.ui.recitationinfo

import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils
import org.quranacademy.quran.recitationsrepository.recitationaudioinfo.SurahAudioInfo

class SurahsAudioInfoDiffUtils(
        oldList: List<SurahAudioInfo>,
        newList: List<SurahAudioInfo>
) : BaseDiffUtils<SurahAudioInfo>(oldList, newList) {

    override fun areItemsTheSame(old: SurahAudioInfo, new: SurahAudioInfo): Boolean {
        return old.surahNumber == new.surahNumber
    }

    override fun areContentsTheSame(old: SurahAudioInfo, new: SurahAudioInfo): Boolean {
        return old.isFullyDownloaded == new.isFullyDownloaded &&
                old.downloadedAudioSizeBytes == new.downloadedAudioSizeBytes
    }

}