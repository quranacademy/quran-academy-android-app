package org.quranacademy.quran.splash.presentation.ui.intro

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_intro.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.presentation.extensions.onPageSelected
import org.quranacademy.quran.presentation.ui.base.BaseActivity
import org.quranacademy.quran.splash.R
import org.quranacademy.quran.splash.presentation.mvp.intro.AppIntroPresenter
import org.quranacademy.quran.splash.presentation.mvp.intro.AppIntroView
import org.quranacademy.quran.splash.presentation.ui.intro.intropages.TafseersIntroFragment
import org.quranacademy.quran.splash.presentation.ui.intro.intropages.TranslationsIntroFragment
import org.quranacademy.quran.splash.presentation.ui.intro.intropages.WordByWordIntroFragment
import org.quranacademy.quran.splash.presentation.ui.splash.SystemLanguageManager
import toothpick.Scope

class AppIntroActivity : BaseActivity(), AppIntroView {

    override val layoutRes = R.layout.activity_intro

    @InjectPresenter
    lateinit var presenter: AppIntroPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<AppIntroPresenter>()

    override val scopeModuleInstaller: (Scope) -> Unit = {
        it.installModule {
            bind(SystemLanguageManager::class.java).singletonInScope()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pagerAdapter = IntroPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(TranslationsIntroFragment.newInstance())
        pagerAdapter.addFragment(TafseersIntroFragment.newInstance())
        pagerAdapter.addFragment(WordByWordIntroFragment.newInstance())
        introPager.adapter = pagerAdapter

        introPager.onPageSelected {
            presenter.onSlideChanged(it)
        }

        skipIntroButton.setOnClickListener {
            presenter.onSkipButtonClicked()
        }
        nextStepButton.setOnClickListener {
            presenter.onNextButtonClicked()
        }
    }

    override fun goToSlide(position: Int) {
        introPager.currentItem = position
    }

    override fun setNextButtontText(text: String) {
        nextStepButton.text = text
    }

}