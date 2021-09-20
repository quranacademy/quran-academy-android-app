package org.quranacademy.quran.search.data

import android.database.Cursor
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.structure.database.FlowCursor
import kotlinx.coroutines.suspendCancellableCoroutine
import org.quranacademy.quran.data.database.models.SearchResultModel
import org.quranacademy.quran.search.domain.SearchResult
import kotlin.coroutines.resume

abstract class BaseSearcher {

    companion object {
        const val HIGHLIGHT_START = "<highlight>"
        const val HIGHLIGHT_END = "</highlight>"
        const val ELLIPSIZE = "<b>...</b>"
    }

    private val adapter by lazy { FlowManager.getModelAdapter(SearchResultModel::class.java) }

    protected suspend fun mapResults(
            resultsCursor: Cursor,
            getDescription: (SearchResultModel) -> String
    ) = suspendCancellableCoroutine<List<SearchResult>> { continuation ->
        continuation.invokeOnCancellation {
            continuation.resume(emptyList())
        }

        val results = (1..resultsCursor.count).map {
            if (continuation.isCancelled) {
                continuation.cancel()
            }
            resultsCursor.moveToNext()
            val model = SearchResultModel()
            adapter.loadFromCursor(FlowCursor.from(resultsCursor), model)
            return@map SearchResult(
                    surahNumber = model.surahNumber,
                    ayahNumber = model.ayahNumber,
                    description = getDescription(model),
                    text = model.text
            )
        }
        resultsCursor.close()
        continuation.resume(results)
    }

    abstract suspend fun search(searchText: String): List<SearchResult>

}