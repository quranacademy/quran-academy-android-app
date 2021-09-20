package org.quranacademy.quran.presentation.mvp

import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.aartikov.alligator.Navigator
import org.quranacademy.quran.di.getGlobal
import org.quranacademy.quran.domain.commons.ResourcesManager
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

open class BasePresenter<V : BaseMvpView> : MvpPresenter<V>(), CoroutineScope {

    private val parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob

    protected val router by lazy { getGlobal<Navigator>() }
    protected val resources by lazy { getGlobal<ResourcesManager>() }
    protected val errorHandler by lazy { getGlobal<ErrorHandler>() }
    protected val resourcesManager by lazy { getGlobal<ResourcesManager>() }

    init {
        Timber.i("HQA-Presenter ${javaClass.canonicalName}")
    }

    override fun onDestroy() {
        parentJob.cancel()
    }

}
