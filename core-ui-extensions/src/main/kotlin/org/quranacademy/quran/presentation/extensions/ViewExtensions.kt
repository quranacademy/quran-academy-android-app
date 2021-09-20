package org.quranacademy.quran.presentation.extensions

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.view.*
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import io.github.inflationx.calligraphy3.TypefaceUtils
import org.quranacademy.quran.di.getGlobal

val Int.dp: Int
    get() = (this * getGlobal<Context>().resources.displayMetrics.density).toInt()

fun Context.inflate(@LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(this).inflate(layoutRes, null, false)
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Fragment.inflate(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup? = null,
        attachToRoot: Boolean = false
): View {
    return LayoutInflater.from(context).inflate(layoutRes, null, attachToRoot)
}

fun Context.inflateThemed(
        @LayoutRes resource: Int,
        @StyleRes themeResId: Int,
        root: ViewGroup? = null,
        attachToRoot: Boolean = false
): View {
    val contextThemeWrapper = ContextThemeWrapper(this, themeResId)
    return LayoutInflater.from(contextThemeWrapper)
            .inflate(resource, root, attachToRoot)
}

fun View.visible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun Dialog.setSize(widthInPercents: Float = -1f, heightInPercents: Float = -1f) {
    val displayMetrics = context.resources.displayMetrics
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.copyFrom(window!!.attributes)

    if (widthInPercents != -1f) {
        val displayWidth = displayMetrics.widthPixels
        val dialogWindowWidth = (displayWidth * widthInPercents).toInt()
        layoutParams.width = dialogWindowWidth
    }

    if (heightInPercents != -1f) {
        val displayHeight = displayMetrics.heightPixels
        val dialogWindowHeight = (displayHeight * heightInPercents).toInt()
        layoutParams.height = dialogWindowHeight
    }

    window!!.attributes = layoutParams
}

fun TextView.applyTypeface(fontPathResId: Int) {
    val context = this.context
    this.typeface = TypefaceUtils.load(context.assets, context.getString(fontPathResId))
}