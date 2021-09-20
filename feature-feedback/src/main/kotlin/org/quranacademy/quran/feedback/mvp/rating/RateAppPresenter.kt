package org.quranacademy.quran.feedback.mvp.rating

import com.arellomobile.mvp.InjectViewState
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.FeedbackScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.OpenGooglePlayScreen
import javax.inject.Inject

@InjectViewState
class RateAppPresenter @Inject constructor(

) : BasePresenter<RateAppView>() {

    fun onRatingSet(rating: Int) {
        if (rating >= MIN_RATING) {
            viewState.showRateOnGooglePlayDialog()
        } else {
            router.goBack()
            router.goForward(FeedbackScreen())
        }
    }

    fun onOpenGooglePlayClicked() {
        router.goBack()
        router.goForward(OpenGooglePlayScreen())
    }

    fun onCancelOpenGooglePlayClicked() {
        router.goBack()
    }

    companion object {
        private const val MIN_RATING = 4
    }

}