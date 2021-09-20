package org.quranacademy.quran.mushaf.presentation.ui.page.ayahtranslationscontainer

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.ayah_translations_panel.view.*
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.domain.models.AppTheme
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.presentation.extensions.inflateThemed
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager

class AyahTranslationsPanel @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    enum class PanelPosition {
        TOP, BOTTOM
    }

    private var panelPosition: PanelPosition = PanelPosition.BOTTOM
    private val childViews by lazy {
        listOf(translationsPanelToolbar, ayahTranslationsContainer)
    }

    var onSwapClickListener: ((panelPosition: PanelPosition) -> Unit)? = null
    var onMenuItemClick: ((menuItemId: Int) -> Unit)? = null
    var isSwapButtonShowing: Boolean = true

    init {
        orientation = VERTICAL

        addChildViews()

        translationsPanelToolbar.inflateMenu(R.menu.translations_panel_menu)
        translationsPanelToolbar.setOnMenuItemClickListener {
            onMenuItemClick?.invoke(it.itemId)
            true
        }

        updateChildViewsOrder()
    }

    fun showSwapButton(isVisible: Boolean) {
        isSwapButtonShowing = isVisible
        updateNavigationButton()
    }

    fun setPanelPosition(position: PanelPosition) {
        panelPosition = position
        updateChildViewsOrder()
    }

    private fun addChildViews() {
        val appearanceManager = getGlobal<AppearanceManager>()
        val nightThemeResId = appearanceManager.getAppThemeResId(AppTheme.NIGHT)
        context.inflateThemed(R.layout.ayah_translations_panel, nightThemeResId, this, true)
    }

    private fun onSwapPanelContentClicked() {
        val newPanelPosition = if (panelPosition == PanelPosition.BOTTOM) {
            PanelPosition.TOP
        } else {
            PanelPosition.BOTTOM
        }
        setPanelPosition(newPanelPosition)
        onSwapClickListener?.invoke(panelPosition)
    }

    private fun updateChildViewsOrder() {
        updateNavigationButton()

        val currentViewsOrder = if (isBottom()) childViews else childViews.reversed()
        removeAllViews()
        currentViewsOrder.forEach { addView(it) }
    }

    private fun updateNavigationButton() {
        if (isSwapButtonShowing) {
            val icon = if (isBottom()) R.drawable.ic_arrow_up_grey_24dp else R.drawable.ic_arrow_down_grey_24dp
            translationsPanelToolbar.setNavigationIcon(icon)
            translationsPanelToolbar.setNavigationOnClickListener {
                onSwapPanelContentClicked()
            }
        } else {
            translationsPanelToolbar.navigationIcon = null
            translationsPanelToolbar.setNavigationOnClickListener(null)
        }
    }

    private fun isBottom() = panelPosition == PanelPosition.BOTTOM

}