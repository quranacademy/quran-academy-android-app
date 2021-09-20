package org.quranacademy.quran.mushaf.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.quranacademy.quran.QuranConstants.QURAN_PAGES_COUNT
import org.quranacademy.quran.mushaf.presentation.ui.page.MushafPageFragment

class MushafPagesAdapter(
        fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val realPageNumber = position + 1
        return MushafPageFragment.newInstance(realPageNumber)
    }

    override fun getCount(): Int {
        return QURAN_PAGES_COUNT
    }

}