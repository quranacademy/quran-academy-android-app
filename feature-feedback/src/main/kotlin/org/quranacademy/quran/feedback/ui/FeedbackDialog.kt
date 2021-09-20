package org.quranacademy.quran.feedback.ui

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_feedback.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.feedback.R
import org.quranacademy.quran.feedback.mvp.feedback.FeedbackPresenter
import org.quranacademy.quran.feedback.mvp.feedback.FeedbackView
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment

class FeedbackDialog : BaseDialogFragment(), FeedbackView {

    @InjectPresenter
    lateinit var presenter: FeedbackPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<FeedbackPresenter>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contentView = inflate(R.layout.dialog_feedback, null)
        return MaterialDialog(activity!!).show {
            title(R.string.leave_feedback_title)
            setSize(widthInPercents = 0.9f)
            customView(view = contentView)
            noAutoDismiss()
            positiveButton(R.string.btn_label_send) {
                presenter.onSendFeedbackClicked(
                        email = contentView.feedbackEmailField.text.toString(),
                        message = contentView.feedbackTextField.text.toString()
                )
            }
        }
    }

}