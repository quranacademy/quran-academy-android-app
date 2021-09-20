package org.quranacademy.quran.bookmarks.ui

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.launch
import org.quranacademy.quran.bookmarks.domain.BookmarkFoldersInteractor
import org.quranacademy.quran.bookmarks.domain.models.BookmarkFolder
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.presentation.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class BookmarkFoldersPresenter @Inject constructor(
        private val ayahId: AyahId,
        private val interactor: BookmarkFoldersInteractor
) : BasePresenter<BookmarkFoldersView>() {

    private lateinit var folders: List<BookmarkFolder>
    private var isFolderSelected: Boolean = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            folders = interactor.getBookmarkFolders()
            val ayahFolderId = interactor.getAyahFolderId(ayahId)
            viewState.showFolders(folders, ayahFolderId)
        }
    }

    fun onFolderSelected(folder: BookmarkFolder) = launch {
        isFolderSelected = true
        interactor.addBookmark(ayahId, folder)
        router.goBackWithResult(BookmarkFoldersScreen.Result(ayahId))
    }

    fun onCreateFolderClicked() {
        viewState.showFolderNameInput()
    }

    fun onNewFolderAdd(name: String) = launch {
        val newFolder = interactor.createFolder(name)
        folders = listOf(*folders.toTypedArray(), newFolder)
        interactor.addBookmark(ayahId, newFolder)
        viewState.showFolders(folders, newFolder.id)
        isFolderSelected = true
    }

    fun onDismiss() {
        if (!isFolderSelected) {
            val defaultFolder = folders.first()
            onFolderSelected(defaultFolder)
        }
    }

}