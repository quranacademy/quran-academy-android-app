package org.quranacademy.quran.mushaf.presentation.ui.ayahtranslation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_ayah_translations.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.di.namedBind
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.AyahDetails
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.di.ayahtranslations.AyahIds
import org.quranacademy.quran.mushaf.presentation.mvp.ayahtranslation.AyahTranslationPresenter
import org.quranacademy.quran.mushaf.presentation.mvp.ayahtranslation.AyahTranslationView
import org.quranacademy.quran.presentation.extensions.inflateThemed
import org.quranacademy.quran.presentation.extensions.onPageSelected
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import toothpick.Scope
import java.io.Serializable

class AyahTranslationsFragment : BaseFragment(), AyahTranslationView {

    companion object {
        private const val AYAH_IDS_ARG = "ayah_ids"

        fun newInstance(ayahIds: List<AyahId>): AyahTranslationsFragment {
            val fragment = AyahTranslationsFragment()
            val args = Bundle()
            args.putSerializable(AYAH_IDS_ARG, ayahIds as Serializable)
            fragment.arguments = args
            return fragment
        }
    }

    override val layoutRes = R.layout.fragment_ayah_translations

    @InjectPresenter
    lateinit var presenter: AyahTranslationPresenter

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val ayahIds = requireArguments().getSerializable(AYAH_IDS_ARG) as List<AyahId>
        scope.installModule {
            namedBind<AyahIds, List<AyahId>>(ayahIds)
        }
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<AyahTranslationPresenter>()

    private val ayahTranslationsAdapter by lazy {
        AyahTranslationsAdapter(requireContext(), appearanceManager, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val nightThemeResId = appearanceManager.getAppThemeResId(AppTheme.NIGHT)
        return requireContext().inflateThemed(layoutRes, nightThemeResId, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openTranslationsScreen.setOnClickListener {
            presenter.onOpenTranslationsScreenClicked()
        }
        prevAyahButton.setOnClickListener {
            presenter.onPrevAyahButtonClicked()
        }
        nextAyahButton.setOnClickListener {
            presenter.onNextAyahButtonClicked()
        }
        ayahTranslationsPager.adapter = ayahTranslationsAdapter
        ayahTranslationsPager.onPageSelected {
            presenter.onAyahChanged(it)
        }
    }

    override fun showProgressLayout(isVisible: Boolean) {
        ayahTranslationLoadingProgress.visible(isVisible)
    }

    override fun updateAyahTranslationsVisibility(isVIsible: Boolean) {
        ayahTranslationsPager.visible(isVIsible)
    }

    override fun showAyahTranslations(translations: List<AyahDetails>) {
        ayahTranslationsAdapter.setData(translations)
    }

    override fun showEnabledTranslationsNotFound(isVisible: Boolean) {
        enabledTranslationsNotFoundLayout.visible(isVisible)
    }

    override fun showNavigationButtons(prevButton: Boolean, nextButton: Boolean) {
        prevAyahButton.visible(prevButton)
        nextAyahButton.visible(nextButton)
    }

    override fun switchToTranslation(currentAyahPosition: Int) {
        ayahTranslationsPager.currentItem = currentAyahPosition
    }

}