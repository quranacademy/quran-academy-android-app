package org.quranacademy.quran.memorization.ui.help

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_memorization_help.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.memorization.R
import org.quranacademy.quran.memorization.mvp.help.MemorizationHelpPresenter
import org.quranacademy.quran.memorization.mvp.help.MemorizationHelpView
import org.quranacademy.quran.presentation.ui.base.BaseThemedActivity

class MemorizationTutorialActivity : BaseThemedActivity(), MemorizationHelpView {

    override val layoutRes = R.layout.activity_memorization_help

    private val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    @InjectPresenter
    lateinit var presenter: MemorizationHelpPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<MemorizationHelpPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setTitle(R.string.quran_memorization_tutorial)

        helpText.movementMethod = LinkMovementMethod.getInstance()
        helpText.text = "Help"
    }

}