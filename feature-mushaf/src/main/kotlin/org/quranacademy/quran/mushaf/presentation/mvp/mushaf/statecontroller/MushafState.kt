package org.quranacademy.quran.mushaf.presentation.mvp.mushaf.statecontroller

sealed class MushafState {
    object AyahsUnselected : MushafState()
    object AyahsSelected : MushafState()
}