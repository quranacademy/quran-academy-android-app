package org.quranacademy.quran.player.presentation.playeroptions.reciterselection

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_reciter_selection.*
import org.quranacademy.quran.di.get
import org.quranacademy.quran.domain.models.Recitation
import org.quranacademy.quran.player.R
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment

class RecitationSelectionDialog : BaseBottomSheetFragment(), RecitationSelectionView {

    override val layoutRes: Int = R.layout.dialog_reciter_selection

    private val adapter = RecitationAdapter {
        presenter.onRecitationSelected(it)
    }

    @InjectPresenter
    lateinit var presenter: RecitationSelectionPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<RecitationSelectionPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_AppTheme_BottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.setOnShowListener {
            Handler().post {
                val bottomSheet = (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recitationToolbar.inflateMenu(R.menu.recitation_selection_menu)
        val searchViewItem = recitationToolbar.menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                presenter.onSearchInput(newText)
                return true
            }

        })

        recitationsList.adapter = adapter
    }

    override fun showRecitations(recitations: List<Recitation>) {
        adapter.updateData(recitations)
    }

}