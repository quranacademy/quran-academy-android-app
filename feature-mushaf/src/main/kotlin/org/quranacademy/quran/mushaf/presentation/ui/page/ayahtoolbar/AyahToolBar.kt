package org.quranacademy.quran.mushaf.presentation.ui.page.ayahtoolbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import org.quranacademy.quran.mushaf.R
import org.quranacademy.quran.presentation.ui.global.getCurrentAppLanguage
import java.lang.RuntimeException

class AyahToolBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private val isRtl = getCurrentAppLanguage().isRtl

    private val menu: Menu = PopupMenu(this.context, this).menu
    private val itemWidth: Int = resources.getDimensionPixelSize(R.dimen.ayah_toolbar_item_width)
    private val pipWidth: Int = resources.getDimensionPixelSize(R.dimen.ayah_toolbar_pip_width)
    private val pipHeight: Int = resources.getDimensionPixelSize(R.dimen.ayah_toolbar_pip_height)
    private val ayahMenu = R.menu.ayah_menu
    private val menuLayout: LinearLayout = LinearLayout(context)
    private val toolBarPip: AyahToolBarPip = AyahToolBarPip(context)

    private var currentMenu: Menu? = null
    private var isShowing: Boolean = false
    private var pipOffset: Float = 0f
    private var pipPosition: PipPosition = PipPosition.DOWN
    private var toolbarPosition: AyahToolBarPosition? = null

    var itemSelectedListener: ((menuItem: MenuItem) -> Unit)? = null

    init {
        val toolBarHeight = resources.getDimensionPixelSize(R.dimen.ayah_toolbar_height)
        menuLayout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, toolBarHeight)
        val background = ContextCompat.getColor(context, R.color.ayah_toolbar_background)
        menuLayout.setBackgroundColor(background)
        addView(menuLayout)

        toolBarPip.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, pipHeight)
        addView(toolBarPip)

        // used to use MenuBuilder, but now it has @RestrictTo, so using this clever trick from
        // StackOverflow - PopupMenu generates a new MenuBuilder internally, so this just lets us
        // get that menu and do whatever we want with it.
        val inflater = MenuInflater(this.context)
        inflater.inflate(ayahMenu, menu)
        showMenu(menu)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val totalWidth = measuredWidth
        val pipWidth = toolBarPip.measuredWidth
        val pipHeight = toolBarPip.measuredHeight
        val menuWidth = menuLayout.measuredWidth
        val menuHeight = menuLayout.measuredHeight

        var pipLeft = pipOffset.toInt()
        if (pipLeft + pipWidth > totalWidth) {
            pipLeft = totalWidth / 2 - pipWidth / 2
        }

        // overlap the pip and toolbar by 1px to avoid occasional gap
        if (pipPosition === PipPosition.UP) {
            toolBarPip.layout(pipLeft, 0, pipLeft + pipWidth, pipHeight + 1)
            menuLayout.layout(0, pipHeight, menuWidth, pipHeight + menuHeight)
        } else {
            toolBarPip.layout(pipLeft, menuHeight - 1, pipLeft + pipWidth, menuHeight + pipHeight)
            menuLayout.layout(0, 0, menuWidth, menuHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(menuLayout, widthMeasureSpec, heightMeasureSpec)
        val width = menuLayout.measuredWidth
        var height = menuLayout.measuredHeight
        measureChild(
                toolBarPip,
                MeasureSpec.makeMeasureSpec(pipWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(pipHeight, MeasureSpec.EXACTLY)
        )
        height += toolBarPip.measuredHeight
        setMeasuredDimension(View.resolveSize(width, widthMeasureSpec),
                View.resolveSize(height, heightMeasureSpec))
    }

    fun getToolBarWidth(): Int {
        // relying on getWidth() may give us the width of a shorter
        // submenu instead of the actual menu
        return menu.size() * itemWidth
    }

    fun setBookmarked(bookmarked: Boolean) {
        val bookmarkItem = menu.findItem(R.id.cab_bookmark_ayah)
        bookmarkItem.setIcon(if (bookmarked) R.drawable.ic_bookmark_white_24dp else R.drawable.ic_bookmark_border_white_24dp)
        val bookmarkButton = findViewById<ImageButton>(R.id.cab_bookmark_ayah)
        bookmarkButton?.setImageDrawable(bookmarkItem.icon)
    }

    fun getPosition(): AyahToolBarPosition? = toolbarPosition

    fun updatePosition(position: AyahToolBarPosition) {
        this.toolbarPosition = position
        setPipPosition(position.pipPosition)
        pipOffset = position.pipOffset
        translationX = (position.x + position.xScroll) -
                (if (isRtl) (parent as ViewGroup).width / 2 else 0)  //for RTL direction we have a strange bug, so we have to do it
        translationY = position.y + position.yScroll

        val needsLayout = position.pipPosition !== pipPosition || pipOffset != position.pipOffset
        if (needsLayout) {
            requestLayout()
        }
    }

    fun updatePositionRelative(position: AyahToolBarPosition) {
        position.x -= pipWidth / 2
        position.y++
        updatePosition(position)
    }


    private fun showMenu(menu: Menu) {
        if (currentMenu === menu) {
            // no need to re-draw
            return
        }

        menuLayout.removeAllViews()
        val count = menu.size()
        for (i in 0 until count) {
            val item = menu.getItem(i)
            if (item.isVisible) {
                val view = getMenuItemView(item)
                menuLayout.addView(view)
            }
        }

        currentMenu = menu
    }

    private fun getMenuItemView(item: MenuItem): View {
        val button = ImageButton(context)
        button.setImageDrawable(item.icon)
        button.setBackgroundResource(R.drawable.ayah_toolbar_button)
        button.id = item.itemId
        button.layoutParams = LayoutParams(itemWidth, LayoutParams.MATCH_PARENT)

        button.setOnClickListener {
            val menuItem = menu.findItem(it.id) ?: return@setOnClickListener
            if (menuItem.hasSubMenu()) {
                showMenu(menuItem.subMenu)
            } else itemSelectedListener?.invoke(menuItem)
        }

        button.setOnLongClickListener {
            val menuItem = menu.findItem(it.id)
            if (menuItem != null && menuItem.title != null) {
                Toast.makeText(context, menuItem.title, Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            return@setOnLongClickListener false
        }
        return button
    }

    private fun setPipPosition(position: PipPosition) {
        pipPosition = position
        toolBarPip.setPosition(position)
    }

    fun isShowing(): Boolean {
        return isShowing
    }

    fun resetMenu() {
        showMenu(menu)
    }

    fun showToolbar() {
        showMenu(menu)
        visibility = View.VISIBLE
        isShowing = true
    }

    fun hideToolbar() {
        isShowing = false
        visibility = View.GONE
    }

    enum class PipPosition {
        UP, DOWN
    }

    data class AyahToolBarPosition(
            var x: Float = 0f,
            var y: Float = 0f,
            var xScroll: Float = 0f,
            var yScroll: Float = 0f,
            var pipOffset: Float = 0f,
            var pipPosition: PipPosition
    )

}