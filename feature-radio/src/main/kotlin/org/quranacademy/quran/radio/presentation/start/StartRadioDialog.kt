package org.quranacademy.quran.radio.presentation.start

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import org.quranacademy.quran.di.get
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment
import org.quranacademy.quran.radio.R

class StartRadioDialog : BaseDialogFragment(), RadioView {

    @InjectPresenter
    lateinit var presenter: RadioPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<RadioPresenter>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(context!!).show {
            title(R.string.quran_academy_radio)
            message(R.string.launch_radio_message)
            negativeButton(R.string.btn_cancel_title)
            positiveButton(R.string.btn_label_ok) {
                presenter.onStartRadioClicked()
            }
        }
    }

}