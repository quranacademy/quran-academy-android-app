package org.quranacademy.quran.mainscreen.presentation.ui.bookmarks

import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_bookmarks_list.*
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem
import org.quranacademy.quran.di.get
import org.quranacademy.quran.mainscreen.R
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarkFolderUiModel
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarksListPresenter
import org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist.BookmarksListView
import org.quranacademy.quran.mainscreen.presentation.ui.ToolbarOwner
import org.quranacademy.quran.presentation.extensions.visible
import org.quranacademy.quran.presentation.ui.base.BaseFragment

class BookmarksFragment : BaseFragment(), BookmarksListView {

    companion object {
        fun newInstance(): BookmarksFragment {
            return BookmarksFragment()
        }
    }

    override val layoutRes = R.layout.fragment_bookmarks_list

    @InjectPresenter
    lateinit var presenter: BookmarksListPresenter

    private val bookmarksAdapter by lazy {
        BookmarksAdapter(
                context = context!!,
                onBookmarkClickListener = { bookmark -> presenter.onBookmarkClick(bookmark) },
                onBookmarkLongClickListener = { bookmark -> presenter.onBookmarkLongClick(bookmark) },
                onFolderClicked = { folder -> presenter.onShowFolderClicked(folder) },
                onFolderOptionsClicked = { folder -> presenter.onShowFolderOptionsClicked(folder) },
                onCreateFolderClicked = { presenter.onCreateFolderClicked() }
        )
    }
    private var toolbarActionMode: ActionMode? = null

    @ProvidePresenter
    fun providePresenter() = scope.get<BookmarksListPresenter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarksList.adapter = bookmarksAdapter
    }

    override fun updateBookmarksVisibility(isVisible: Boolean) {
        bookmarksList.visible(isVisible)
    }

    override fun showBookmarksListEmptyLayout(isVisible: Boolean) {
        bookmarksListIsEmptyLayout.visible(isVisible)
    }

    override fun showProgressLayout(isVisible: Boolean) {
        progressLayout.visible(isVisible)
    }

    override fun showFolderOptions(folder: BookmarkFolderUiModel) {
        MaterialDialog(context!!).show {
            title(text = folder.name)
            val surahTitles = listOf(
                    getString(R.string.rename_bookmark_folder),
                    getString(R.string.delete_bookmark_folder)
            )
            listItems(items = surahTitles) { _, index, _ ->
                when (index) {
                    0 -> presenter.onRenameFolderClicked(folder)
                    1 -> presenter.onDeleteFolderClicked(folder)
                }
            }
        }
    }

    override fun showFolderRenamingInput(folder: BookmarkFolderUiModel) {
        MaterialDialog(context!!).show {
            title(text = folder.name)
            input(prefill = folder.name) { _, text ->
                presenter.onFolderRenamed(folder, text.toString())
            }
            positiveButton(org.quranacademy.quran.bookmarks.R.string.btn_label_ok)
        }
    }

    override fun showFolderCreatingInput() {
        MaterialDialog(requireContext()).show {
            title(R.string.enter_folder_name)
            input { _, text ->
                presenter.onCreateFolderInput(text.toString())
            }
            positiveButton(org.quranacademy.quran.bookmarks.R.string.btn_label_ok)
        }
    }

    override fun showFolderDeletingConfirm(folder: BookmarkFolderUiModel) {
        MaterialDialog(requireContext()).show {
            title(text = folder.name)
            message(R.string.bookmark_folder_deleting_message)
            positiveButton(R.string.btn_label_ok) {
                presenter.onDeleteFolderConfirmed(folder)
            }
            negativeButton(R.string.btn_cancel_title)
        }
    }

    override fun showBookmarks(bookmarks: List<BookmarkItem>, isUpdating: Boolean) {
        if (!isUpdating) {
            bookmarksAdapter.setData(bookmarks)
        } else {
            bookmarksAdapter.updateData(bookmarks)
        }
    }

    override fun startSelectionMode(selectedBookmarks: List<BookmarkItem>) {
        toolbarActionMode = getToolbar().startActionMode(object : ActionMode.Callback {

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                presenter.onSelectionModeFinished()
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                mode.menuInflater.inflate(R.menu.bookmarks_selection_menu, menu)
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                onActionModeItemClick(item)
                return true
            }
        })
        bookmarksAdapter.highlightSelectedItems(selectedBookmarks)
    }

    override fun updateSelectedBookmarks(selectedBookmarks: List<BookmarkItem>) {
        bookmarksAdapter.highlightSelectedItems(selectedBookmarks)
    }

    override fun stopSelectionMode() {
        toolbarActionMode?.finish()
    }

    private fun getToolbar() = (activity as ToolbarOwner).provideToolbar()

    private fun onActionModeItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.action_delete_bookmark -> presenter.onDeleteSelectedBookmarksClicked()
        }
    }

}
