package org.quranacademy.quran.bookmarks.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_bookmark_folders.view.*
import org.quranacademy.quran.bookmarks.R
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.bookmarks.ui.adapter.BookmarkFoldersAdapter
import org.quranacademy.quran.di.bind
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.installModule
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.ui.base.BaseBottomSheetFragment
import toothpick.Scope

class BookmarkFoldersDialog : BaseBottomSheetFragment(), BookmarkFoldersView {

    override val layoutRes: Int = R.layout.dialog_bookmark_folders

    override val scopeModuleInstaller: (Scope) -> Unit = { scope ->
        val screen = screenResolver.getScreen<BookmarkFoldersScreen>(this)
        scope.installModule {
            bind(AyahId::class)
                    .toInstance(screen.ayahId)
        }
    }

    @InjectPresenter
    lateinit var presenter: BookmarkFoldersPresenter

    @ProvidePresenter
    fun providePresenter() = scope.get<BookmarkFoldersPresenter>()

    private val bookmarkFoldersAdapter by lazy {
        BookmarkFoldersAdapter(
                onCreateFolderClicked = { presenter.onCreateFolderClicked() },
                onFolderSelected = { presenter.onFolderSelected(it) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().bookmarkFolders.adapter = bookmarkFoldersAdapter
    }

    override fun showFolders(folders: List<BookmarkFolder>, ayahFolderId: Long) {
        bookmarkFoldersAdapter.setData(folders, ayahFolderId)
    }

    override fun showFolderNameInput() {
        MaterialDialog(requireContext()).show {
            title(R.string.enter_bookmark_folder_name)
            input { _, text ->
                presenter.onNewFolderAdd(text.toString())
            }
            positiveButton(R.string.btn_label_ok)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        presenter.onDismiss()
    }

}