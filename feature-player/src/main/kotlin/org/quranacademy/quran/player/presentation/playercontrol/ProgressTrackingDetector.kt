package org.quranacademy.quran.player.presentation.playercontrol

import android.widget.SeekBar

class ProgressTrackingDetector(
        private val onTrackingStarted: () -> Unit,
        private val onProgressFinished: (Int) -> Unit
) : SeekBar.OnSeekBarChangeListener {

    var isProgressTracking: Boolean = false
        private set

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromTouch: Boolean) {

    }

    /**
     * Когда пользователь начал двигать ползунок прогрессбара
     */
    override fun onStartTrackingTouch(seekBar: SeekBar) {
        //отключаем обновление прогрессбара во время движения ползунка
        isProgressTracking = true
        onTrackingStarted()
    }

    /**
     * Когда пользователь перестал двигать ползунок прогрессбара
     */
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        isProgressTracking = false
        onProgressFinished(seekBar.progress)
    }

}