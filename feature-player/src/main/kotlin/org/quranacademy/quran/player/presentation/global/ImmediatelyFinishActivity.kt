package org.quranacademy.quran.player.presentation.global

import android.app.Activity
import android.os.Bundle

class ImmediatelyFinishActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }

}