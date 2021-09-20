package org.quranacademy.quran.presentation.ui.base

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.ViewPumpAppCompatDelegate
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.aartikov.alligator.*
import me.aartikov.alligator.exceptions.ActivityResolvingException
import me.aartikov.alligator.exceptions.NavigationException
import org.quranacademy.quran.App
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.di.DI
import org.quranacademy.quran.di.get
import org.quranacademy.quran.di.objectScopeName
import org.quranacademy.quran.domain.commons.SystemManager
import org.quranacademy.quran.presentation.mvp.routing.screens.OpenGooglePlayScreen
import org.quranacademy.quran.presentation.ui.appearance.AppearanceManager
import org.quranacademy.quran.presentation.ui.appearance.LanguageManager
import org.quranacademy.quran.presentation.ui.languagesystem.ContextLocaleConfigurator
import org.quranacademy.quran.presentation.ui.languagesystem.Philology
import org.quranacademy.quran.presentation.ui.languagesystem.android.PhilologyResources
import org.quranacademy.quran.presentation.ui.languagesystem.fallback.FallbacksTree
import org.quranacademy.quran.presentation.ui.languagesystem.utils.MenuLocalizer
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity : MvpAppCompatActivity(), CoroutineScope {

    companion object {
        private const val STATE_LAUNCH_FLAG = "state_launch_flag"
        private const val STATE_SCOPE_NAME = "state_scope_name"
        private const val STATE_SCOPE_WAS_CLOSED = "state_scope_was_closed"
    }

    abstract val layoutRes: Int

    protected val parentJob = Job() //or SupevisorJob???
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    private val appScope = Toothpick.openScope(DI.APP_SCOPE)
    protected lateinit var scope: Scope
    lateinit var scopeName: String
    protected open val scopeModuleInstaller: (Scope) -> Unit = {}

    protected val navigator by lazy { appScope.get<Navigator>() }
    protected val navigationContextBinder by lazy { appScope.get<NavigationContextBinder>() }
    protected val screenResolver by lazy { appScope.get<ScreenResolver>() }
    protected val appearanceManager by lazy { appScope.get<AppearanceManager>() }
    protected val languageManager by lazy { appScope.get<LanguageManager>() }
    protected val systemManager by lazy { appScope.get<SystemManager>() }
    private val appCompatDelegate: AppCompatDelegate by lazy {
        ViewPumpAppCompatDelegate(
                super.getDelegate(),
                this
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val appLanguage = languageManager.getCurrentAppLanguage().code
        val isLanguageSupported = FallbacksTree.isLanguageSupported(appLanguage)
        val realAppLanguage = if (isLanguageSupported) {
            appLanguage
        } else {
            FallbacksTree.getFallbackFor(appLanguage) ?: FallbacksTree.DEFAULT_LANGUAGE
        }
        val customLocaleContext = ContextLocaleConfigurator.configContext(newBase, realAppLanguage)
        val philologyContextWrapper = Philology.wrap(customLocaleContext)
        val viewPumpContextWrapper = ViewPumpContextWrapper.wrap(philologyContextWrapper)
        super.attachBaseContext(viewPumpContextWrapper)
    }

    override fun getApplicationContext(): Context {
        return Philology.wrap(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return PhilologyResources(super.getResources())
    }

    override fun getDelegate(): AppCompatDelegate {
        return appCompatDelegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val savedAppCode = savedInstanceState?.getString(STATE_LAUNCH_FLAG)
        //False - if fragment was restored without new app process (for example: activity rotation)
        val isNewInAppProcess = savedAppCode != getAppLaunchCode()
        val scopeWasClosed = savedInstanceState?.getBoolean(STATE_SCOPE_WAS_CLOSED) ?: true
        val scopeIsNotInitialized = isNewInAppProcess || scopeWasClosed
        scopeName = savedInstanceState?.getString(STATE_SCOPE_NAME) ?: objectScopeName()
        scope = Toothpick.openScopes(DI.APP_SCOPE, scopeName)
                .apply {
                    if (scopeIsNotInitialized) {
                        Timber.d("Init new UI scope: $scopeName")
                        scopeModuleInstaller(this)
                    } else {
                        Timber.d("Get exist UI scope: $scopeName")
                    }
                }

        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }

    override fun onResume() {
        super.onResume()

        systemManager.attachActivity(this)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigationContextBinder.bind(createNavigationContext().build())
    }

    override fun onPause() {
        navigationContextBinder.unbind(this)
        systemManager.detachActivity()

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        parentJob.cancel()

        if (needCloseScope()) {
            //destroy this fragment with scope
            Timber.d("Destroy UI scope: $scopeName")
            Toothpick.closeScope(scope.name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_SCOPE_NAME, scopeName)
        outState.putString(STATE_LAUNCH_FLAG, getAppLaunchCode())
        outState.putBoolean(STATE_SCOPE_WAS_CLOSED, needCloseScope()) //save it but will be used only if destroyed
    }

    open fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun Menu.localizeMenu() {
        MenuLocalizer.localize(this@BaseActivity, this)
    }

    //these methods are created to customize it in child classes if it needed
    protected open fun createNavigationContext(): NavigationContext.Builder {
        return NavigationContext.Builder(this, (navigator as AndroidNavigator).navigationFactory)
                .navigationErrorListener { onNavigationError(it) }
                .screenResultListener { screenClass: Class<out Screen>, result: ScreenResult? ->
                    onScreenResult(screenClass, result)
                }
    }

    /**
     * @param result Can be null if a screen has finished without no result.
     */
    protected open fun onScreenResult(screenClass: Class<out Screen>, result: ScreenResult?) {

    }

    fun getAppLaunchCode(): String = getApp().getAppLaunchCode()

    fun getApp() = super.getApplicationContext() as App

    //It will be valid only for 'onDestroy()' method
    private fun needCloseScope(): Boolean =
            when {
                isChangingConfigurations -> false
                isFinishing -> true
                else -> false
            }

    private fun onNavigationError(error: NavigationException?) {
        if (error is ActivityResolvingException && error.screen is OpenGooglePlayScreen) {
            Toast.makeText(this, R.string.google_play_app_not_found_error_message, Toast.LENGTH_SHORT).show()
        } else {
            throw RuntimeException(error)
        }
    }

}