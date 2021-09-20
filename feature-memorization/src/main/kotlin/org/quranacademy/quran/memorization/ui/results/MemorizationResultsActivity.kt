package org.quranacademy.quran.memorization.ui.results

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import org.quranacademy.quran.di.get
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.memorization.mvp.results.MemorizationResultsPresenter
import org.quranacademy.quran.memorization.mvp.results.MemorizationResultsView
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity

class MemorizationResultsActivity : BaseThemedActivity(), MemorizationResultsView {

    override val layoutRes = R.layout.activity_memorization_results

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    @InjectPresenter
    lateinit var presenter: MemorizationResultsPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MemorizationResultsPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.tajweed_rules)
    }

}