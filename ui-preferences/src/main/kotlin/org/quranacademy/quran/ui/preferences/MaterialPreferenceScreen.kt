package org.quranacademy.quran.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import org.quranacademy.quran.ui.preferences.io.MaterialPreferences
import org.quranacademy.quran.ui.preferences.io.StorageModule
import org.quranacademy.quran.ui.preferences.io.UserInputModule

class MaterialPreferenceScreen @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var container: LinearLayout
    private var isContainerPrepared = false

    var userInputModule: UserInputModule = MaterialPreferences.getUserInputModule(context)
        set(userInputModule) {
            field = userInputModule
            setUserInputModuleRecursively(container, userInputModule)
        }
    var storageModule: StorageModule = MaterialPreferences.getStorageModule(context)
        set(storageModule) {
            field = storageModule
            setStorageModuleRecursively(container, storageModule)
        }

    init {
        isFillViewport = true
        container = LinearLayout(context)
        container.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        container.orientation = LinearLayout.VERTICAL
        addView(container)

        isContainerPrepared = true
    }

    fun changeViewsVisibility(viewIds: List<Int>, visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        changeViewsVisibility(container, viewIds, visibility)
    }

    fun setVisibilityController(controllerId: Int, controlledIds: List<Int>, showWhenChecked: Boolean) {
        setVisibilityController(
                findViewById<View>(controllerId) as AbsMaterialCheckablePreference,
                controlledIds, showWhenChecked
        )
    }

    fun setVisibilityController(
            controller: AbsMaterialCheckablePreference,
            controlledIds: List<Int>,
            showWhenChecked: Boolean
    ) {
        val shouldShow = if (showWhenChecked) controller.getValue() else !controller.getValue()
        val initialVisibility = if (shouldShow) View.VISIBLE else View.GONE
        changeViewsVisibility(this, controlledIds, initialVisibility)
        controller.addPreferenceClickListener(OnClickListener {
            val shouldShow = if (showWhenChecked) controller.getValue() else !controller.getValue()
            val visibility = if (shouldShow) View.VISIBLE else View.GONE
            changeViewsVisibility(this@MaterialPreferenceScreen,
                    controlledIds,
                    visibility)
        })
    }

    private fun changeViewsVisibility(container: ViewGroup, viewIds: Collection<Int>, visibility: Int) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is ViewGroup) {
                if (viewIds.contains(child.getId())) {
                    child.setVisibility(visibility)
                } else if (child !is AbsMaterialPreference<*>) {
                    changeViewsVisibility(child, viewIds, visibility)
                }
            }
            if (viewIds.contains(child.id)) {
                child.visibility = visibility
            }
        }
    }

    private fun setUserInputModuleRecursively(container: ViewGroup, module: UserInputModule) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is AbsMaterialPreference<*>) {
                child.setUserInputModule(module)
            } else if (child is ViewGroup) {
                setUserInputModuleRecursively(child, module)
            }
        }
    }

    private fun setStorageModuleRecursively(container: ViewGroup, module: StorageModule) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            if (child is AbsMaterialPreference<*>) {
                child.setStorageModule(module)
            } else if (child is ViewGroup) {
                setStorageModuleRecursively(child, module)
            }
        }
    }

    override fun addView(child: View) {
        if (isContainerPrepared) {
            container.addView(child)
        } else {
            super.addView(child)
        }
    }

    override fun addView(child: View, index: Int) {
        if (isContainerPrepared) {
            container.addView(child, index)
        } else {
            super.addView(child, index)
        }
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isContainerPrepared) {
            container.addView(child, params)
        } else {
            super.addView(child, params)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isContainerPrepared) {
            container.addView(child, index, params)
        } else {
            super.addView(child, index, params)
        }
    }

}
