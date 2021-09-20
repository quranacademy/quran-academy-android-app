package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks

import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkCategory
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkFolderUiModel
import org.quranacademy.quran.presentation.ui.global.BaseDiffUtils

class BookmarkDiffUtils(
        oldList: List<Any>,
        newList: List<Any>
) : BaseDiffUtils<Any>(oldList, newList) {

    override fun areItemsTheSame(old: Any, new: Any): Boolean {
        compare<BookmarkFolderUiModel>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<RecentReadingPlace>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<PageBookmark>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<AyahBookmark>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<BookmarkCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

    override fun areContentsTheSame(old: Any, new: Any): Boolean {
        compare<BookmarkFolderUiModel>(old, new) { one, two ->
            return one.id == two.id
                    && one.name == two.name
                    && one.bookmarkCount == two.bookmarkCount
                    && one.isExpanded == two.isExpanded
        }
        compare<RecentReadingPlace>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<PageBookmark>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<AyahBookmark>(old, new) { one, two ->
            return one.id == two.id
        }
        compare<BookmarkCategory>(old, new) { one, two ->
            return one.title == two.title
        }
        return false
    }

}