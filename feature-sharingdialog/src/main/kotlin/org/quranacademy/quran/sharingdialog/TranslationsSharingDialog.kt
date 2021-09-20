package org.quranacademy.quran.sharingdialog

import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_translation_selection.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.di.namedBind
import org.quranacademy.quran.di.typedBind
import org.quranacademy.quran.domain.models.Translation
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import toothpick.Scope

class TranslationsSharingDialog: BaseBottomSheetFragment(), TranslationsForSharingView {

    override val layoutRes: Int = R.layout.dialog_translation_selection

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val screen = screenResolver.getScreen<TranslationsSharingScreen>(this)
        scope.installModule {
            typedBind(screen.type)
            namedBind("translations", screen.translations)
            namedBind("ayahs", screen.ayahs)
        }
    }

    @InjectPresenter
    lateinit var presenter: TranslationsForSharingPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<TranslationsForSharingPresenter>()

    private val adapter = TranslationsAdapter {
        presenter.onTranslationSelected(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        translationsList.adapter = adapter
        selectButton.setOnClickListener {
            presenter.onFinishClicked()
        }
    }

    override fun showTranslations(translations: List<TranslationSharingModel>) {
        adapter.updateData(translations)
    }

    override fun updateButtonState(isEnabled: Boolean) {
        selectButton.isEnabled = isEnabled
    }

}