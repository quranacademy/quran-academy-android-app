package org.quranacademy.quran.mainscreen.domain.bookmarkslist

import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace

class BookmarksWrapper(
        val recentPlaces: List<RecentReadingPlace>,
        val pageBookmarks: List<PageBookmark>,
        val ayahBookmark: List<AyahBookmark>
)