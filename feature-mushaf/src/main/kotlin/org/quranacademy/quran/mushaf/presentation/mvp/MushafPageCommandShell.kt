package org.quranacademy.quran.mushaf.presentation.mvp

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import javax.inject.Inject

class MushafPageCommandShell @Inject constructor() {

    private val ayahClicks = BroadcastChannel<AyahClickEvent>(1)
    private val highlightAyahsCommand = BroadcastChannel<HighlightAyahsCommand>(1)

    fun ayahClicks() = ayahClicks.asFlow()

    suspend fun onAyahClick(ayahId: AyahId, clickType: AyahClickEvent.ClickType, pageNumber: Int) {
        ayahClicks.send(AyahClickEvent(ayahId, clickType, pageNumber))
    }

    fun highlightAyahsCommands() = highlightAyahsCommand.asFlow()

    suspend fun highlightAyahs(
            pageNumber: Int,
            selectedAyahs: List<AyahId>,
            highlightType: AyahHighlightType,
            addToOld: Boolean
    ) {
        highlightAyahsCommand.send(HighlightAyahsCommand(
                pageNumber = pageNumber,
                selectedAyahs = selectedAyahs,
                highlightType = highlightType,
                isHighlighted = true,
                addToOld = addToOld
        ))
    }

    suspend fun unhighlightAyahs(
            pageNumber: Int,
            selectedAyahs: List<AyahId>,
            highlightType: AyahHighlightType
    ) {
        highlightAyahsCommand.send(
                HighlightAyahsCommand(
                        pageNumber = pageNumber,
                        selectedAyahs = selectedAyahs,
                        highlightType = highlightType,
                        isHighlighted = false,
                        addToOld = false
                )
        )
    }

}