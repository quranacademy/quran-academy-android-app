package org.quranacademy.quran.feedback.mvp.feedback

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.data.appinforepository.AppInfoRepository
import org.quranacademy.quran.domain.commons.EmailValidator
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.Feedback
import org.quranacademy.quran.feedback.R
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import javax.inject.Inject

@InjectViewState
class FeedbackPresenter @Inject constructor(
        private val appInfoRepository: AppInfoRepository,
        private val emailValidator: EmailValidator
) : BasePresenter<FeedbackView>() {

    fun onSendFeedbackClicked(email: String, message: String) = launch {
        if (email.isNotEmpty() && !emailValidator.isValidEmail(email)) {
            viewState.showMessage(resourcesManager.getString(R.string.incorrect_email_message))
            return@launch
        }

        if (message.isEmpty()) {
            viewState.showMessage(resourcesManager.getString(R.string.empty_feedback_message))
            return@launch
        }

        val feedback = Feedback(
                email = if (email.isNotEmpty()) email else null,
                text = message
        )
        try {
            appInfoRepository.sendFeedback(feedback)
            val feedbackSentMessage = resourcesManager.getString(R.string.feedback_sent_message)
            viewState.showMessage(feedbackSentMessage)
            router.goBack()
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

}