package org.quranacademy.quran.domain.models.bounds

data class AyahBounds(
        val surahNumber: Int,
        val ayahNumber: Int,
        val lineNumber: Int,
        val position: Int,
        var minX: Int,
        var minY: Int,
        var maxX: Int,
        var maxY: Int
) {

    fun engulf(bounds: AyahBounds) {
        if (bounds.minX < bounds.maxX && bounds.minY < bounds.maxY) {
            if (this.minX < this.maxX && this.minY < this.maxY) {
                if (this.minX > bounds.minX)
                    this.minX = bounds.minX
                if (this.minY > bounds.minY)
                    this.minY = bounds.minY
                if (this.maxX < bounds.maxX)
                    this.maxX = bounds.maxX
                if (this.maxY < bounds.maxY)
                    this.maxY = bounds.maxY
            } else {
                this.minX = bounds.minX
                this.minY = bounds.minY
                this.maxX = bounds.maxX
                this.maxY = bounds.maxY
            }
        }
    }

    fun contains(x: Int, y: Int): Boolean {
        return (minX < maxX && minY < maxY  // check for empty first
                && x >= minX && x < maxX && y >= minY && y < maxY)
    }

}