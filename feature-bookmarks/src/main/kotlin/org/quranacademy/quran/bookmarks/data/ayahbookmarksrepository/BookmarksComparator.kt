package org.quranacademy.quran.bookmarks.data.ayahbookmarksrepository

import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark

class BookmarksComparator : Comparator<AyahBookmark> {

    override fun compare(o1: AyahBookmark, o2: AyahBookmark): Int {
        val surahComp = o1.surahNumber.compareTo(o2.surahNumber)

        if (surahComp != 0) {
            return surahComp
        }

        return o1.ayahNumber.compareTo(o2.ayahNumber)
    }

}