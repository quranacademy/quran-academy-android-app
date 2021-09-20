package org.quranacademy.quran.search.presentation

import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_search.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.extensions.hideKeyboard
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.presentation.ui.global.OnScrollListener
import org.quranacademy.quran.presentation.ui.global.toArabicNumberIfNeeded
import org.quranacademy.quran.search.R
import org.quranacademy.quran.search.domain.QuranSearchResults
import org.quranacademy.quran.search.domain.SearchTranslationFilter
import org.quranacademy.quran.search.presentation.adapter.SearchResultsAdapter

class SearchActivity : BaseThemedActivity(), QuranSearchView {

    override val layoutRes: Int = R.layout.activity_search

    private val searchResultsAdapter by lazy {
        SearchResultsAdapter(
                appearanceManager = appearanceManager,
                languageManager = languageManager,
                onClickListener = { presenter.onOpenSearchResultClicked(it) }
        )
    }

    @InjectPresenter
    lateinit var presenter: QuranSearchPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<QuranSearchPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.inflateMenu(R.menu.search_menu)
        toolbar.setOnMenuItemClickListener { onMenuItemClick(it.itemId) }
        toolbar.menu.localizeMenu()

        searchResultsList.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        searchResultsList.adapter = searchResultsAdapter
        searchResultsList.addOnScrollListener(OnScrollListener(OnScrollListener.State.STARTED) {
            hideKeyboard()
        })

        searchInput.addTextChangedListener(EditChangeDebounce(this) {
            presenter.onSearchInput(it)
        })
    }

    override fun showSearchTranslations(filters: List<SearchTranslationFilter>) {
        MaterialDialog(this).show {
            title(R.string.translations_sharing_dialog_title)
            val filterTitles = filters.map { it.name }
            val selectedIndexes = filters
                    .mapIndexed { index, filter -> index to filter }
                    .filter { it.second.isSelected }
                    .map { it.first }
            listItemsMultiChoice(
                    items = filterTitles,
                    initialSelection = selectedIndexes.toIntArray()
            ) { _, indices, _ ->
                val selectedTranslations = indices.map { filters[it] }
                presenter.onSearchTranslationsSelected(selectedTranslations)
            }

            positiveButton(R.string.btn_label_ok)
        }
    }

    override fun hideFiltersButton() {
        toolbar.menu.findItem(R.id.search_filters).isVisible = false
    }

    override fun showProgress(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun showEmptySearch(isVisible: Boolean) {
        searchResultsCountLabel.text = getString(R.string.search_results_count_label, 0.toArabicNumberIfNeeded())
        emptySearchLayout.visible(isVisible)
    }

    override fun showResults(isVisible: Boolean) {
        searchResultsList.visible(isVisible)
    }

    override fun showResults(searchResults: QuranSearchResults) {
        searchResultsContainer.visible(true)
        val resultsCount = searchResults.results.size.toArabicNumberIfNeeded()
        searchResultsCountLabel.text = getString(R.string.search_results_count_label, resultsCount)
        searchResultsAdapter.setData(searchResults)
    }

    override fun showEmptyResults(queryText: String) {
        noResultsLabel.visible(true)
        noResultsLabel.text = getString(R.string.no_results_label, queryText)
    }

    override fun hideEmptyResults() {
        noResultsLabel.visible(false)
    }

    private fun onMenuItemClick(itemId: Int): Boolean {
        when (itemId) {
            R.id.search_filters -> {
                presenter.onOpenSearchFiltersClicked()
            }
        }
        return true
    }

}