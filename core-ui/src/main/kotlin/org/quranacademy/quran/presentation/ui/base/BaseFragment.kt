package org.quranacademy.quran.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.quranacademy.quran.App
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.objectScopeName
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : MvpAppCompatFragment(), CoroutineScope {

    companion object {
        private const val STATE_LAUNCH_FLAG = "state_launch_flag"
        private const val STATE_SCOPE_NAME = "state_scope_name"
        private const val STATE_SCOPE_WAS_CLOSED = "state_scope_was_closed"
    }

    abstract val layoutRes: Int

    protected val parentJob = Job() //or SupevisorJob???
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    private var instanceStateSaved: Boolean = false
    protected open val parentScopeName: String by lazy {
        (parentFragment as? BaseFragment)?.scopeName
                ?: (activity as BaseActivity).scopeName
    }
    protected lateinit var scope: Scope
    lateinit var scopeName: String
    protected open val scopeModuleInstaller: (Scope) -> Unit = {}

    private val appScope = Toothpick.openScope(DI.APP_SCOPE)
    protected val appearanceManager by lazy { appScope.get<AppearanceManager>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(layoutRes, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedAppCode = savedInstanceState?.getString(STATE_LAUNCH_FLAG)
        //False - if fragment was restored without new app process (for example: activity rotation)
        val isNewInAppProcess = savedAppCode != getAppLaunchCode()
        val scopeWasClosed = savedInstanceState?.getBoolean(STATE_SCOPE_WAS_CLOSED) ?: true
        val scopeIsNotInit = isNewInAppProcess || scopeWasClosed
        scopeName = savedInstanceState?.getString(STATE_SCOPE_NAME) ?: objectScopeName()
        scope = Toothpick.openScopes(parentScopeName, scopeName)
                .apply {
                    if (scopeIsNotInit) {
                        Timber.d("Init new UI scope: $scopeName")
                        scopeModuleInstaller(this)
                    } else {
                        Timber.d("Get exist UI scope: $scopeName")
                    }
                }
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onDestroy() {
        super.onDestroy()

        if (needCloseScope()) {
            //destroy this fragment with scope
            Timber.d("Destroy UI scope: $scopeName")
            Toothpick.closeScope(scope.name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        instanceStateSaved = true
        outState.putString(STATE_SCOPE_NAME, scopeName)
        outState.putString(STATE_LAUNCH_FLAG, getAppLaunchCode())
        outState.putBoolean(STATE_SCOPE_WAS_CLOSED, needCloseScope()) //save it but will be used only if destroyed
    }

    fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getAppLaunchCode(): String = (requireActivity() as BaseActivity).getAppLaunchCode()

    //This is android, baby!
    private fun isRealRemoving(): Boolean =
            (isRemoving && !instanceStateSaved) //because isRemoving == true for fragment in backstack on screen rotation
                    || ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    //It will be valid only for 'onDestroy()' method
    private fun needCloseScope(): Boolean =
            when {
                activity?.isChangingConfigurations == true -> false
                activity?.isFinishing == true -> true
                else -> isRealRemoving()
            }

}
