package org.quranacademy.quran.presentation.ui.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import org.quranacademy.quran.presentation.extensions.inflateThemed

abstract class BaseBottomSheetFragment : BaseDialogFragment() {

    abstract val layoutRes: Int

    var onShowListener: (() -> Unit)? = null
    var onDismissListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), this.theme)
        dialog.setOnShowListener {
            onShowListener?.invoke()
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onDismissListener?.invoke()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentThemeId = (requireActivity() as BaseThemedActivity).currentThemeId
        return requireContext().inflateThemed(layoutRes, currentThemeId, container)
    }

}
