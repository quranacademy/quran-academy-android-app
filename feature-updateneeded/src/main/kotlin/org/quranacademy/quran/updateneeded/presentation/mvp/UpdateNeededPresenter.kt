package org.quranacademy.quran.updateneeded.presentation.mvp

import com.arellomobile.mvp.InjectViewState
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.OpenGooglePlayScreen
import javax.inject.Inject

@InjectViewState
class UpdateNeededPresenter @Inject constructor(

) : BasePresenter<UpdateNeededView>() {

    fun onDownloadAppButtonClicked() {
        router.goForward(OpenGooglePlayScreen())
    }

    fun onOpenGooglePlayError() {
        router.goForward(OpenGooglePlayScreen(true))
    }

}