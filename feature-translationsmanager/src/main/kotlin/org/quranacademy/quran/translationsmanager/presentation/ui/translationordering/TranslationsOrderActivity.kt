package org.quranacademy.quran.translationsmanager.presentation.ui.translationordering

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.thesurix.gesturerecycler.GestureAdapter
import com.thesurix.gesturerecycler.GestureManager
import kotlinx.android.synthetic.main.activity_translations_order.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering.TranslationOrderedUIModel
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering.TranslationsOrderPresenter
import org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering.TranslationsSortingView
import org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.adapter.TranslationsAdapter
import org.quranacademy.quran.translationsmanager.presentation.ui.translationordering.adapter.TranslationsOrderDiffUtils

class TranslationsOrderActivity : BaseThemedActivity(), TranslationsSortingView {

    override val layoutRes = R.layout.activity_translations_order

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val translationsAdapter = TranslationsAdapter()

    @InjectPresenter
    lateinit var presenter: TranslationsOrderPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<TranslationsOrderPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { navigator.finish() }
        toolbar.setTitle(R.string.prefs_translations_ordering_title)

        translationsList.adapter = translationsAdapter
        GestureManager.Builder(translationsList)
                .setManualDragEnabled(true)
                .setDragFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
                .build()

        translationsAdapter.setDataChangeListener(object : GestureAdapter.OnDataChangeListener<Any> {
            override fun onItemRemoved(item: Any, position: Int) {}

            override fun onItemReorder(item: Any, fromPos: Int, toPos: Int) {
                if (item is TranslationOrderedUIModel) {
                    presenter.onItemReordered(fromPos, toPos)
                }
            }
        })
    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun showTranslations(translations: List<TranslationItem>) {
        val diffUtils = TranslationsOrderDiffUtils(translationsAdapter.data as List<TranslationItem>, translations)
        translationsList.post {
            translationsAdapter.setData(translations, diffUtils)
        }
    }

    override fun showTranslationsListEmpty() {
        translationListEmptyView.visible(true)
    }

}