package org.quranacademy.quran.mainscreen.presentation.mvp.bookmarkslist

import com.arellomobile.mvp.InjectViewState
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.aartikov.alligator.Screen
import org.joda.time.DateTime
import org.quranacademy.quran.bookmarks.domain.models.AyahBookmark
import org.quranacademy.quran.bookmarks.domain.models.BookmarkItem
import org.quranacademy.quran.bookmarks.domain.models.PageBookmark
import org.quranacademy.quran.bookmarks.domain.models.RecentReadingPlace
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.mainscreen.domain.bookmarkslist.BookmarksListInteractor
import org.quranacademy.quran.mainscreen.domain.bookmarkslist.BookmarksWrapper
import org.quranacademy.quran.presentation.extensions.replace
import org.quranacademy.quran.presentation.mvp.BasePresenter
import org.quranacademy.quran.presentation.mvp.routing.screens.MushafScreen
import org.quranacademy.quran.presentation.mvp.routing.screens.SurahDetailsScreen
import javax.inject.Inject

@InjectViewState
class BookmarksListPresenter @Inject constructor(
        private val interactor: BookmarksListInteractor,
        private val fodersMapper: BookmarkFolderUiMapper
) : BasePresenter<BookmarksListView>() {

    private var isSelectionMode = false
    private val selectedBookmarks = mutableListOf<BookmarkItem>()
    private lateinit var ayahBookmarkFolders: MutableList<BookmarkFolderUiModel>
    private lateinit var bookmarks: BookmarksWrapper

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        launch {
            interactor.getBookmarksUpdates()
                    .collect { loadBookmarksList(true) }
        }

        launch {
            interactor.getBookmarkFolderUpdates()
                    .collect { loadBookmarksList(true) }
        }

        launch {
            interactor.getLanguageChanges()
                    .consumeEach { loadBookmarksList() }
        }

        launch {
            loadBookmarksList()
        }
    }

    fun onShowFolderClicked(folder: BookmarkFolderUiModel) {
        val folderWithNewState = folder.copy(isExpanded = !folder.isExpanded)
        ayahBookmarkFolders.replace(folder, folderWithNewState) { first, second ->
            first.id == second.id
        }
        showBookmarks(true)
    }

    fun onShowFolderOptionsClicked(folder: BookmarkFolderUiModel) {
        viewState.showFolderOptions(folder)
    }

    fun onRenameFolderClicked(folder: BookmarkFolderUiModel) {
        viewState.showFolderRenamingInput(folder)
    }

    fun onFolderRenamed(folder: BookmarkFolderUiModel, name: String) = launch {
        val renamedFolder = folder.copy(name = name)
        interactor.saveFolder(fodersMapper.mapFrom(renamedFolder))
        ayahBookmarkFolders.replace(folder, renamedFolder) { first, second ->
            first.id == second.id
        }
        showBookmarks(true)
    }

    fun onDeleteFolderClicked(folder: BookmarkFolderUiModel) = launch {
        viewState.showFolderDeletingConfirm(folder)
    }

    fun onDeleteFolderConfirmed(folder: BookmarkFolderUiModel) = launch {
        interactor.deleteFolder(fodersMapper.mapFrom(folder))
        ayahBookmarkFolders.remove(folder)
        showBookmarks(true)
    }

    fun onBookmarkClick(bookmark: BookmarkItem) {
        if (isSelectionMode) {
            if (bookmark !is RecentReadingPlace) {
                addToSelectedOrRemove(bookmark)
            }
        } else {
            openBookmark(bookmark)
        }
    }

    fun onBookmarkLongClick(bookmark: BookmarkItem) {
        if (!isSelectionMode) {
            isSelectionMode = true
            selectedBookmarks.add(bookmark)
            viewState.startSelectionMode(selectedBookmarks.toList())
        } else {
            addToSelectedOrRemove(bookmark)
        }
    }

    fun onSelectionModeFinished() {
        selectedBookmarks.clear()
        viewState.updateSelectedBookmarks(selectedBookmarks)
        viewState.stopSelectionMode()
        isSelectionMode = false
    }

    fun onDeleteSelectedBookmarksClicked() = launch {
        interactor.deleteBookmarks(selectedBookmarks.toList())
        updateBookmarksList()

        selectedBookmarks.clear()
        isSelectionMode = false
        viewState.stopSelectionMode()
        viewState.updateSelectedBookmarks(selectedBookmarks)
    }

    fun onCreateFolderClicked() {
        viewState.showFolderCreatingInput()
    }

    fun onCreateFolderInput(folderName: String) = launch {
        val newFolder = BookmarkFolderUiModel(
                0,
                folderName,
                DateTime(),
                bookmarkCount = 0,
                isDefaultFolder = false,
                isExpanded = false
        )
        interactor.saveFolder(fodersMapper.mapFrom(newFolder))
        ayahBookmarkFolders.add(newFolder)
        showBookmarks(true)
    }

    private suspend fun loadBookmarksList(isUpdating: Boolean = false) {
        viewState.updateBookmarksVisibility(false)
        viewState.showProgressLayout(true)
        bookmarks = interactor.getBookmarksList()
        ayahBookmarkFolders = fodersMapper.mapTo(bookmarks.ayahBookmark, interactor.getFolders()).toMutableList()
        viewState.showProgressLayout(false)
        showBookmarks(isUpdating)
    }

    private suspend fun updateBookmarksList() {
        bookmarks = interactor.getBookmarksList()
        showBookmarks(true)
    }

    private fun showBookmarks(
            isUpdating: Boolean = false
    ) {
        val isBookmarksListIsEmpty =
                bookmarks.recentPlaces.isEmpty() &&
                        bookmarks.pageBookmarks.isEmpty() &&
                        bookmarks.ayahBookmark.isEmpty()
        if (isBookmarksListIsEmpty) {
            viewState.showBookmarksListEmptyLayout(true)
            viewState.updateBookmarksVisibility(false)
        } else {
            viewState.showBookmarksListEmptyLayout(false)
            viewState.updateBookmarksVisibility(true)

            val categorizedBookmarks = prepareBookmarksList()
            viewState.showBookmarks(categorizedBookmarks, isUpdating)
        }
    }

    private fun prepareBookmarksList(): List<BookmarkItem> {
        val categorizedBookmarks = mutableListOf<BookmarkItem>()

        if (bookmarks.recentPlaces.isNotEmpty()) {
            val recentPlaceTitle = resourcesManager.getString(R.string.recent_pages)
            categorizedBookmarks.add(BookmarkCategory(recentPlaceTitle))
            categorizedBookmarks.addAll(bookmarks.recentPlaces)
        }

        if (bookmarks.pageBookmarks.isNotEmpty()) {
            val pageBookmarksTitle = resourcesManager.getString(R.string.page_bookmarks)
            categorizedBookmarks.add(BookmarkCategory(pageBookmarksTitle))
            categorizedBookmarks.addAll(bookmarks.pageBookmarks)
        }

        ayahBookmarkFolders.forEach { folder ->
            val folderBookmarks = bookmarks.ayahBookmark.filter { it.folderId == folder.id }
            categorizedBookmarks.add(folder)
            if (folder.isExpanded) {
                categorizedBookmarks.addAll(folderBookmarks)
            }
        }

        categorizedBookmarks.add(CreateFolderModel)

        return categorizedBookmarks
    }

    private fun addToSelectedOrRemove(bookmark: BookmarkItem) {
        if (selectedBookmarks.contains(bookmark)) {
            selectedBookmarks.remove(bookmark)
        } else {
            selectedBookmarks.add(bookmark)
        }
        viewState.updateSelectedBookmarks(selectedBookmarks.toList())

        if (selectedBookmarks.isEmpty()) {
            viewState.stopSelectionMode()
        }
    }

    private fun openBookmark(bookmark: BookmarkItem) = launch {
        when (bookmark) {
            is RecentReadingPlace -> {
                interactor.saveLastReadPosition(bookmark.surahNumber, bookmark.ayahNumber, bookmark.isMushafMode)
                val readingScreen: Screen = if (bookmark.isMushafMode) MushafScreen() else SurahDetailsScreen()
                router.goForward(readingScreen)
            }
            is PageBookmark -> {
                interactor.saveLastReadPositionForPage(bookmark.pageNumber)
                router.goForward(MushafScreen())
            }
            is AyahBookmark -> {
                interactor.saveLastReadPositionForAyah(bookmark.surahNumber, bookmark.ayahNumber)
                router.goForward(SurahDetailsScreen())
            }
        }
    }

}