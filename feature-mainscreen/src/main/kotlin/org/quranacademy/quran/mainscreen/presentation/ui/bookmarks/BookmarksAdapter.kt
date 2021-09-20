package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkFolderUiModel
import org.quranacademy.quran.mainscreen.presentation.ui.bookmarks.delegates.*

class BookmarksAdapter(
        val context: Context,
        onBookmarkClickListener: (BookmarkItem) -> Unit,
        onBookmarkLongClickListener: (BookmarkItem) -> Unit,
        onFolderClicked: (BookmarkFolderUiModel) -> Unit,
        onFolderOptionsClicked: (BookmarkFolderUiModel) -> Unit,
        onCreateFolderClicked: () -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private var selectedBookmarks = emptyList<BookmarkItem>()

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(BookmarkCategoryAdapterDelegate())

        val readingHistoryAdapterDelegate = ReadingHistoryAdapterDelegate(
                onClickListener = onBookmarkClickListener
        )
        delegatesManager.addDelegate(readingHistoryAdapterDelegate)

        val pageBookmarkDelegate = PageBookmarkAdapterDelegate(
                onClickListener = onBookmarkClickListener,
                onLongClickListener = onBookmarkLongClickListener,
                isItemSelected = { selectedBookmarks.contains(it) }
        )
        delegatesManager.addDelegate(pageBookmarkDelegate)

        val ayahBookmarkDelegate = AyahBookmarkAdapterDelegate(
                onClickListener = onBookmarkClickListener,
                onLongClickListener = onBookmarkLongClickListener,
                isItemSelected = { selectedBookmarks.contains(it) }
        )
        delegatesManager.addDelegate(ayahBookmarkDelegate)

        val bookmarkFolder = BookmarkFolderAdapterDelegate(
                onFolderClicked = onFolderClicked,
                onFolderOptionsClicked = onFolderOptionsClicked
        )
        delegatesManager.addDelegate(bookmarkFolder)

        delegatesManager.addDelegate(BookmarkCreateFolderAdapterDelegate(onCreateFolderClicked))
    }

    fun setData(bookmarks: List<BookmarkItem>) {
        items = bookmarks.toMutableList()

        notifyDataSetChanged()
    }

    fun updateData(updatedBookmarks: List<BookmarkItem>) {
        val diffUtilCallback = BookmarkDiffUtils(items, updatedBookmarks)
        val productDiffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items = updatedBookmarks.toMutableList()
        productDiffResult.dispatchUpdatesTo(this)
    }

    fun highlightSelectedItems(selectedBookmarks: List<BookmarkItem>) {
        this.selectedBookmarks = selectedBookmarks
        notifyDataSetChanged()
    }

}
