package org.quranacademy.quran.bookmarks.ui

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface BookmarkFoldersView : BaseMvpView {

    fun showFolders(folders: List<BookmarkFolder>, ayahFolderId: Long)

    fun showFolderNameInput()

}