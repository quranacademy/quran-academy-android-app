package org.quranacademy.quran.memorization.ui.test

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import org.quranacademy.quran.di.get
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.memorization.mvp.test.MemorizationTestPresenter
import org.quranacademy.quran.memorization.mvp.test.MemorizationTestView
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity

class MemorizationTestActivity : BaseThemedActivity(), MemorizationTestView {

    override val layoutRes = R.layout.activity_memorization_test

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    @InjectPresenter
    lateinit var presenter: MemorizationTestPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MemorizationTestPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.quran_memorizatio_test)
    }

}