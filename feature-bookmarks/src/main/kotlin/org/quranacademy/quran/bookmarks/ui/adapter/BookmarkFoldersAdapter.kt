package org.quranacademy.quran.bookmarks.ui.adapter

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder

class BookmarkFoldersAdapter(
        onCreateFolderClicked: () -> Unit,
        onFolderSelected: (BookmarkFolder) -> Unit
) : ListDelegationAdapter<MutableList<Any>>() {

    private val folderAdapterDelegate = BookmarkFolderAdapterDelegate(
            onFolderSelected = onFolderSelected
    )

    init {
        delegatesManager.addDelegate(CreateNewFolderAdapterDelegate(onCreateFolderClicked))
        delegatesManager.addDelegate(folderAdapterDelegate)
    }

    fun setData(folders: List<BookmarkFolder>, ayahFolderId: Long) {
        items = mutableListOf(NewFolderItem, *folders.toTypedArray())
        folderAdapterDelegate.setBookmarkFolderId(ayahFolderId)
        notifyDataSetChanged()
    }

}
