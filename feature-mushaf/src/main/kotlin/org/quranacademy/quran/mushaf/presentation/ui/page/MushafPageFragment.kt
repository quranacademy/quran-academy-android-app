package org.quranacademy.quran.mushaf.presentation.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_mushaf_page.*
import org.quranacademy.quran.di.bindPrimitive
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.FileDownloadInfo
import org.quranacademy.quran.domain.models.QuranPage
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.mushaf.di.mushafpage.PageNumber
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.AyahHighlightType
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.MushafPagePresenter
import org.quranacademy.quran.mushaf.presentation.mvp.mushafpage.MushafPageView
import org.quranacademy.quran.mushaf.presentation.ui.MushafActivity
import org.quranacademy.quran.presentation.extensions.inflateThemed
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import toothpick.Scope
import java.util.*

class MushafPageFragment : BaseFragment(), MushafPageView {

    companion object {

        private const val PAGE_NUMBER_ARG = "page_number"

        fun newInstance(pageNumber: Int): MushafPageFragment {
            val fragment = MushafPageFragment()
            fragment.arguments = Bundle().apply {
                putInt(PAGE_NUMBER_ARG, pageNumber)
            }
            return fragment
        }

    }

    override val layoutRes = R.layout.fragment_mushaf_page

    @InjectPresenter
    lateinit var presenter: MushafPagePresenter

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val pageNumber = requireArguments().getInt(PAGE_NUMBER_ARG)
        scope.installModule {
            bindPrimitive(PageNumber::class, pageNumber)
        }
    }

    @ProvidePresenter
    fun providePresenter() = scope.get<MushafPagePresenter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val currentMushafThemeId = appearanceManager.getCurrentMushafThemeResId()
        return requireContext().inflateThemed(layoutRes, currentMushafThemeId, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retryLoadPage.setOnClickListener {
            presenter.onRetryLoadPageClicked()
        }

        pageLayout.mushafPageImage.onPageTouchListener = {
            presenter.onPageClickEvent(it)
        }
        pageLayout.onScrollListener = { x: Int, y: Int, oldx: Int, oldy: Int ->
            (activity as MushafActivity).onPageVerticalScroll(y)
        }

        val currentTheme = appearanceManager.getCurrentMushafTheme()
        if (currentTheme == AppTheme.NIGHT) {
            pageLayout.enableNightMode(255f)
        }
    }

    override fun showImageDownloadProgress(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun updateImageDownloadProgress(downloadInfo: FileDownloadInfo) {
        //not implemented intentionally
    }

    override fun showImageDownloadError(isVisible: Boolean) {
        loadErrorLayout.visible(isVisible)
    }

    override fun showPage(pageInfo: QuranPage) {
        pageLayout.setPageInfo(pageInfo)
    }

    override fun updateAyahHighlighting(ayahHighlights: SortedMap<AyahHighlightType, MutableSet<AyahId>>) {
        pageLayout.updateAyahHighlighting(ayahHighlights)
    }

}