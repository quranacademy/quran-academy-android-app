package org.quranacademy.quran.splash.presentation.ui.intro.intropages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.intro_header.*
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.splash.R

abstract class IntroPageFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_intro

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        introSlideTitle.text = getTitle()

        val fragment = getContentFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.introPageContentContainer, fragment, fragment::class.java.simpleName)
                .commit()
    }

    abstract fun getTitle(): String

    abstract fun getContentFragment(): Fragment

}
