package org.quranacademy.quran.mainscreen.presentation.ui.surahslist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_surahs_list.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.surahslist.QuranJuz
import org.quranacademy.quran.mainscreen.presentation.mvp.surahslist.SurahsListPresenter
import org.quranacademy.quran.mainscreen.presentation.mvp.surahslist.SurahsListView
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment
import org.quranacademy.quran.presentation.ui.global.OnScrollListener

class SurahsFragment : BaseFragment(), SurahsListView {

    companion object {

        fun newInstance(): SurahsFragment {
            return SurahsFragment()
        }
    }

    override val layoutRes = R.layout.fragment_surahs_list

    private val surahsAdapter by lazy {
        SurahsAdapter(
                onSurahClickListener = { presenter.onSurahSelected(it) },
                onJuzClickListener = { presenter.onJuzSelected(it) }
        )
    }

    @InjectPresenter
    lateinit var presenter: SurahsListPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<SurahsListPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surahsList.adapter = surahsAdapter
        surahsList.addOnScrollListener(OnScrollListener(OnScrollListener.State.ENDED) {
            presenter.saveLastScrollPosition(getSurahsListLayoutManager().findFirstCompletelyVisibleItemPosition())
        })
    }

    override fun updateSurahsVisibility(isVisible: Boolean) {
        surahsList.visible(isVisible)
    }

    override fun showSurahs(surahsGroupedByJuz: List<QuranJuz>) {
        surahsAdapter.setData(surahsGroupedByJuz)
    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun scrollTo(position: Int) {
        surahsList.post {
            getSurahsListLayoutManager().scrollToPositionWithOffset(position, 20)
        }
    }

    private fun getSurahsListLayoutManager() = (surahsList.layoutManager as LinearLayoutManager)

}