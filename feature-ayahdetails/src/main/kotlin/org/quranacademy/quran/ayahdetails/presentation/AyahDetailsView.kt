package org.quranacademy.quran.ayahdetails.presentation

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.mvp.BaseMvpView

@StateStrategyType(AddToEndSingleStrategy::class)
interface AyahDetailsView : BaseMvpView {

    fun showProgressLayout(isVisible: Boolean)

    fun showAyahDetails(ayah: AyahDetails)

    fun showTranslationsListEmptyLabel()

}