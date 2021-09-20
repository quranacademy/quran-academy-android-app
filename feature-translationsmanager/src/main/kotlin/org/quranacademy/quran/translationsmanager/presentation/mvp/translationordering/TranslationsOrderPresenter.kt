package org.quranacademy.quran.translationsmanager.presentation.mvp.translationordering

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.domain.commons.ResourcesManager
import org.quranacademy.quran.domain.models.TranslationOrder
import org.quranacademy.quran.presentation.extensions.moveItem
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.ErrorHandler
import org.quranacademy.quran.translationsmanager.R
import org.quranacademy.quran.translationsmanager.domain.TranslationsOrderInteractor
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationCategory
import org.quranacademy.quran.translationsmanager.presentation.mvp.global.TranslationItem
import javax.inject.Inject

@InjectViewState
class TranslationsOrderPresenter @Inject constructor(
        private val interactor: TranslationsOrderInteractor,
        private val translationUIModelMapper: TranslationOrderedUIModelMapper
) : BasePresenter<TranslationsSortingView>() {

    private lateinit var sortedTranslations: MutableList<TranslationItem>
    private val showInListCategory by lazy { TranslationCategory(resourcesManager.getString(R.string.show_in_list_title)) }
    private val showInDialogCategory by lazy { TranslationCategory(resourcesManager.getString(R.string.show_in_dialog_title)) }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        loadTranslationsList()
    }

    private fun loadTranslationsList() = launch {
        try {
            viewState.showProgressLayout(true)
            val order = interactor.getTranslationsOrder()
            val enabledTranslations = interactor.getEnabledTranslations()
            val languages = interactor.getLanguages()
            val orderUiModels = translationUIModelMapper.mapToUIModel(order, enabledTranslations, languages)
                    .sortedBy { it.order }

            sortedTranslations = listOf(
                    showInListCategory,
                    *orderUiModels.filter { !it.showInDialog }.toTypedArray(),
                    showInDialogCategory,
                    *orderUiModels.filter { it.showInDialog }.toTypedArray()
            ).toMutableList()
            viewState.showProgressLayout(false)
            viewState.showTranslations(sortedTranslations)
        } catch (error: Exception) {
            errorHandler.proceed(error, viewState::showMessage)
        }
    }

    fun onItemReordered(fromPos: Int, toPos: Int) = launch {
        sortedTranslations.moveItem(fromPos, toPos)

        if (toPos == 0) {
            //не разрешаем помещать перевод над заголовком "Show in list"
            //и делаем так, чтобы аголовок "Show in list" всегда был в начале
            sortedTranslations.moveItem(1, 0)
        }
        val showInListPosition = sortedTranslations.indexOf(showInListCategory)
        val showInDialogPosition = sortedTranslations.indexOf(showInDialogCategory)

        val showInListItems = sortedTranslations
                .subList(showInListPosition + 1, showInDialogPosition)
                .map { it as TranslationOrderedUIModel }
                .mapIndexed { index, translationOrder ->
                    TranslationOrder(
                            translationCode = translationOrder.translationCode,
                            showInDialog = false,
                            order = index
                    )
                }
        val showInDialogItems = sortedTranslations
                .subList(showInDialogPosition + 1, sortedTranslations.size)
                .map { it as TranslationOrderedUIModel }
                .mapIndexed { index, translationOrder ->
                    TranslationOrder(
                            translationCode = translationOrder.translationCode,
                            showInDialog = true,
                            order = showInListItems.size + index
                    )
                }
        viewState.showTranslations(sortedTranslations)

        interactor.saveTranslationsOrder(showInListItems + showInDialogItems)
    }

}