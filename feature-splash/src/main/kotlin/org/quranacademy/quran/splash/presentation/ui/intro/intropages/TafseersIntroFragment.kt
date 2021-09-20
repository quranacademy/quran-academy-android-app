package org.quranacademy.quran.splash.presentation.ui.intro.intropages

import org.quranacademy.quran.splash.R
import org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.TranslationsManagerFragment

class TafseersIntroFragment : IntroPageFragment() {

    companion object {

        fun newInstance(): TafseersIntroFragment {
            return TafseersIntroFragment()
        }

    }

    override fun getTitle(): String = getString(R.string.tafseers_intro_title)

    override fun getContentFragment() = TranslationsManagerFragment.newInstance(true, true)

}