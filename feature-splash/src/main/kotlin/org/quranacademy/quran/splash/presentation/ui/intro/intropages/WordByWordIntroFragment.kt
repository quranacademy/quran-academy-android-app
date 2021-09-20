package org.quranacademy.quran.splash.presentation.ui.intro.intropages

import org.quranacademy.quran.splash.R
import org.quranacademy.quran.wordbywordmanager.presentation.ui.WordByWordTranslationsManagerFragment

class WordByWordIntroFragment : IntroPageFragment() {

    companion object {

        fun newInstance(): WordByWordIntroFragment {
            return WordByWordIntroFragment()
        }

    }

    override fun getTitle(): String = getString(R.string.wbw_translations_intro_title)

    override fun getContentFragment() = WordByWordTranslationsManagerFragment.newInstance(true)

}