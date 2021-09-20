package org.quranacademy.quran.memorization.mvp.test

import com.arellomobile.mvp.InjectViewState
import org.quranacademy.quran.presentation.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class MemorizationTestPresenter @Inject constructor(

) : BasePresenter<MemorizationTestView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

    }

}