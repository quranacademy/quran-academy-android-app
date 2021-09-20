package org.quranacademy.quran.mushaf.domain.page

import android.util.SparseArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.domain.models.bounds.AyahBounds
import timber.log.Timber
import javax.inject.Inject

class AyahByPositionFinder @Inject constructor() {

    suspend fun getAyahIdByPosition(
            x: Int,
            y: Int,
            coordinates: Map<AyahId, List<AyahBounds>>
    ): AyahId = withContext(Dispatchers.Unconfined) {
        var closestLine = -1
        var closestDelta = -1

        val lineAyahs = SparseArray<MutableList<AyahId>>()
        val keys = coordinates.keys
        //ToDo: заменить на foreach
        for (key in keys) {
            val bounds = coordinates[key] ?: continue

            for (b in bounds) {
                // only one AyahBound will exist for an ayah on a particular line
                val line = b.lineNumber
                var items: MutableList<AyahId>? = lineAyahs.get(line)
                if (items == null) {
                    items = ArrayList()
                }
                items.add(key)
                lineAyahs.put(line, items)
                if (b.contains(x, y)) {
                    return@withContext key
                }

                val delta = Math.min(Math.abs(b.maxY - y),
                        Math.abs(b.minY - y))
                if (closestDelta == -1 || delta < closestDelta) {
                    closestLine = b.lineNumber
                    closestDelta = delta
                }
            }
        }

        if (closestLine > -1) {
            var leastDeltaX = -1
            var closestAyah: AyahId? = null
            val ayah = lineAyahs.get(closestLine)
            if (ayah != null) {
                Timber.d("no exact match, %d candidates.", ayah.size)
                //ToDo: дать нормальные имена
                for (ayah in ayah) {
                    val bounds = coordinates[ayah] ?: continue
                    for (b in bounds) {
                        if (b.lineNumber > closestLine) {
                            // this is the last ayah in ayat list
                            break
                        }

                        if (b.lineNumber == closestLine) {
                            // if x is within the x of this ayah, that's our answer
                            if (b.maxX >= x && b.minX <= x) {
                                return@withContext ayah
                            }

                            // otherwise, keep track of the least delta and return it
                            val delta = Math.min(Math.abs(b.maxX - x),
                                    Math.abs(b.minX - x))
                            if (leastDeltaX == -1 || delta < leastDeltaX) {
                                closestAyah = ayah
                                leastDeltaX = delta
                            }
                        }
                    }
                }
            }

            closestAyah?.let {
                Timber.d("fell back to closest ayah of %s", it)
                return@withContext it
            }
        }

        throw AyahByPositionNotFoundException()
    }

}