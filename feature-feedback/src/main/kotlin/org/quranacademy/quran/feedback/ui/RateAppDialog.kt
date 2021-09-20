package org.quranacademy.quran.feedback.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.RatingBar
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_rate_app.view.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.feedback.R
import org.quranacademy.quran.feedback.mvp.rating.RateAppPresenter
import org.quranacademy.quran.feedback.mvp.rating.RateAppView
import org.quranacademy.quran.presentation.extensions.inflate
import org.quranacademy.quran.presentation.extensions.setSize
import org.quranacademy.quran.presentation.ui.base.BaseDialogFragment

class RateAppDialog : BaseDialogFragment(), RateAppView {

    @InjectPresenter
    lateinit var presenter: RateAppPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<RateAppPresenter>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contentView = inflate(R.layout.dialog_rate_app)

        contentView.ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            presenter.onRatingSet(rating.toInt())
        }

        return MaterialDialog(activity!!).show {
            title(R.string.leave_feedback_title)
            setSize(widthInPercents = 0.85f)
            customView(view = contentView)
        }
    }

    override fun showRateOnGooglePlayDialog() {
        MaterialDialog(activity!!).show {
            title(R.string.please_rate_us_title)
            message(R.string.please_rate_us_message)
            positiveButton(R.string.btn_label_ok) { presenter.onOpenGooglePlayClicked() }
            negativeButton(R.string.btn_cancel_title) { presenter.onCancelOpenGooglePlayClicked() }
        }
    }

}