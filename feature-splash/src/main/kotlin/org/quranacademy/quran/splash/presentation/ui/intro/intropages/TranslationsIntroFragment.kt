package org.quranacademy.quran.splash.presentation.ui.intro.intropages

import org.quranacademy.quran.splash.R
import org.quranacademy.quran.translationsmanager.presentation.ui.translationsmanager.TranslationsManagerFragment

class TranslationsIntroFragment : IntroPageFragment() {

    companion object {

        fun newInstance(): TranslationsIntroFragment {
            return TranslationsIntroFragment()
        }

    }

    override fun getTitle(): String = getString(R.string.translations_intro_title)

    override fun getContentFragment() = TranslationsManagerFragment.newInstance(false, true)

}