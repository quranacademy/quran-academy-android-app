package org.quranacademy.quran.bookmarks.ui

import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.AyahId
import java.io.Serializable

class BookmarkFoldersScreen(
        val ayahId: AyahId
) : Screen, Serializable {

    class Result(val ayahId: AyahId) : ScreenResult

}