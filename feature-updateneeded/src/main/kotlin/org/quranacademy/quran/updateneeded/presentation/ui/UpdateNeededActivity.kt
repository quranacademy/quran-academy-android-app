package org.quranacademy.quran.updateneeded.presentation.ui

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_update_needed.*
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.exceptions.ActivityResolvingException
import me.aartikov.alligator.exceptions.NavigationException
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.mvp.routing.screens.OpenGooglePlayScreen
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.updateneeded.R
import org.quranacademy.quran.updateneeded.presentation.mvp.UpdateNeededPresenter
import org.quranacademy.quran.updateneeded.presentation.mvp.UpdateNeededView

class UpdateNeededActivity : BaseThemedActivity(), UpdateNeededView {

    override val layoutRes = R.layout.activity_update_needed

    @InjectPresenter
    lateinit var presenter: UpdateNeededPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<UpdateNeededPresenter>()

    override fun createNavigationContext(): NavigationContext.Builder {
        return NavigationContext.Builder(this, (navigator as AndroidNavigator).navigationFactory)
                .navigationErrorListener { onNavigationError(it) }
    }

    private fun onNavigationError(it: NavigationException) {
        if (it is ActivityResolvingException && it.screen is OpenGooglePlayScreen) {
            presenter.onOpenGooglePlayError()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateAppButton.setOnClickListener { presenter.onDownloadAppButtonClicked() }
    }

}