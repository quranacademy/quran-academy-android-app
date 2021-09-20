package org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface BookmarksListView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun updateBookmarksVisibility(isVisible: Boolean)

    fun showBookmarksListEmptyLayout(isVisible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showFolderOptions(folder: BookmarkFolderUiModel)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showFolderRenamingInput(folder: BookmarkFolderUiModel)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showFolderCreatingInput()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showFolderDeletingConfirm(folder: BookmarkFolderUiModel)

    fun showBookmarks(
            bookmarks: List<BookmarkItem>,
            isUpdating: Boolean
    )

    fun startSelectionMode(selectedBookmarks: List<BookmarkItem>)

    fun updateSelectedBookmarks(selectedBookmarks: List<BookmarkItem>)

    fun stopSelectionMode()

}