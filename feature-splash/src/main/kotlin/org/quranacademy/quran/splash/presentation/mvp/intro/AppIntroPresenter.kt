package org.quranacademy.quran.splash.presentation.mvp.intro

import com.arellomobile.mvp.InjectViewState
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MainScreen
import org.quranacademy.quran.splash.R
import javax.inject.Inject

@InjectViewState
class AppIntroPresenter @Inject constructor(

) : BasePresenter<AppIntroView>() {

    companion object {
        const val SLIDES_COUNT = 3
    }

    private var currentSlidePosition = 0

    fun onSlideChanged(position: Int) {
        currentSlidePosition = position
        if (isLastSlide()) {
            viewState.setNextButtontText(resourcesManager.getString(R.string.intro_done_button))
        } else {
            viewState.setNextButtontText(resourcesManager.getString(R.string.intro_next_button))
        }
    }

    fun onNextButtonClicked() {
        val isLastItem = isLastSlide()
        if (isLastItem) {
            finishIntro()
        } else {
            viewState.goToSlide(++currentSlidePosition)
        }
    }

    fun onSkipButtonClicked() {
        finishIntro()
    }

    private fun isLastSlide() = currentSlidePosition == SLIDES_COUNT - 1

    private fun finishIntro() {
        router.replace(MainScreen())
    }


}