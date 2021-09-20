package org.quranacademy.quran.player.presentation.global

import androidx.fragment.app.FragmentManager
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResult
import org.quranacademy.quran.domain.models.AyahId
import org.quranacademy.quran.player.presentation.playercontrol.PlayerControlFragment
import org.quranacademy.quran.player.presentation.playercontrol.dynamicsettings.PlayerDynamicSettingsScreen
import org.quranacademy.quran.player.presentation.playeroptions.PlayerOptionsFragment
import org.quranacademy.quran.presentation.mvp.routing.screens.AyahSelectionScreen

class QuranPlayer(
        private val fragmentManager: FragmentManager
) {

    companion object {
        const val PLAYER_OPTIONS_TAG = "PlayerOptionsFragment"
        const val PLAYER_CONTROL_TAG = "PlayerControlFragment"
    }

    var playerOptionsDialog: PlayerOptionsFragment? = null
    var playerControlDialog: PlayerControlFragment? = null
    var onPlayerOptionsVisibilityChanged: ((isVisible: Boolean) -> Unit)? = null
    var onPlayerControlVisibilityChanged: ((isVisible: Boolean) -> Unit)? = null

    fun showPlayerOptionsPanel(startAyah: AyahId, endAyah: AyahId) {
        val playerOptionsDialog = PlayerOptionsFragment.newInstance(startAyah, endAyah)
        playerOptionsDialog.onShowListener = {
            onPlayerOptionsVisibilityChanged?.invoke(true)
        }
        playerOptionsDialog.onDismissListener = {
            onPlayerOptionsVisibilityChanged?.invoke(false)
        }
        playerOptionsDialog.show(fragmentManager, PLAYER_OPTIONS_TAG)
        this.playerOptionsDialog = playerOptionsDialog
    }

    fun showPlayerControlPanel() {
        val playerControlDialog = PlayerControlFragment()
        playerControlDialog.onShowListener = {
            onPlayerControlVisibilityChanged?.invoke(true)
        }
        playerControlDialog.onDismissListener = {
            onPlayerControlVisibilityChanged?.invoke(false)
        }
        playerControlDialog.show(fragmentManager, PLAYER_CONTROL_TAG)
        this.playerControlDialog = playerControlDialog
    }

    fun hidePlayerOptionsPanel() {
        playerOptionsDialog?.dismiss()
    }

    fun hidePlayerControlPanel() {
        playerControlDialog?.dismiss()
    }

    fun onScreenResult(screenClass: Class<out Screen>, result: ScreenResult?): Boolean {
        if (result is AyahSelectionScreen.Result) {
            val fragment = fragmentManager.findFragmentByTag(PLAYER_OPTIONS_TAG)
            (fragment as PlayerOptionsFragment?)?.onAyahSelected(result.ayahId, result.type)
            return true
        } else if (result is PlayerDynamicSettingsScreen.Result) {
            val fragment = fragmentManager.findFragmentByTag(PLAYER_CONTROL_TAG)
            (fragment as PlayerControlFragment?)?.onPlayerOptionsChanged(result.options)
            return true
        }
        return false
    }

}