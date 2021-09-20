package org.quranacademy.quran.core.permissions

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.rationale.ConfirmCallback
import com.afollestad.assent.rationale.RationaleHandler
import com.afollestad.assent.rationale.Requester
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.presentation.mvp.routing.screens.AppInfoScreen

internal class HQAPermissionsRationaleHandler(
        private val context: Activity,
        //private val onOkButtonClicked: () -> Unit,
        requester: Requester
) : RationaleHandler(context, requester) {

    override fun showRationale(
            permission: Permission,
            message: CharSequence,
            confirm: ConfirmCallback
    ) {
        MaterialDialog(context).show {
            title(R.string.permission_needed_title)
            message(R.string.external_permission_not_needed_message)
            positiveButton(R.string.btn_label_ok) {
                confirm(true)
            }
            onDismiss { confirm(false) }
        }
    }

    override fun onDestroy() = Unit
}

fun Fragment.createHQAPermissionsRationale(
        //onOkButtonClicked: () -> Unit,
        block: RationaleHandler.() -> Unit
): RationaleHandler {
    return HQAPermissionsRationaleHandler(
            context = activity ?: throw IllegalStateException("Fragment not attached"),
            requester = ::askForPermissions
    ).apply(block)
}

fun Activity.createHQAPermissionsRationale(
        //onOkButtonClicked: () -> Unit,
        block: RationaleHandler.() -> Unit
): RationaleHandler {
    return HQAPermissionsRationaleHandler(
            context = this,
            requester = ::askForPermissions
    ).apply(block)
}

fun showGrantPermissionFromSettingsDialog(context: Context) {
    MaterialDialog(context).show {
        title(R.string.permission_needed_title)
        message(R.string.external_permission_not_granted_message)
        positiveButton(R.string.btn_label_ok) {
            getGlobal<Navigator>().goForward(AppInfoScreen())
        }
    }
}