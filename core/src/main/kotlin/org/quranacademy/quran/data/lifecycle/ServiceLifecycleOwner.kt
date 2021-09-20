package org.quranacademy.quran.data.lifecycle

class ServiceLifecycleOwner {

    private val lifecycleObservers = mutableListOf<ServiceLifecycleObserver>()

    fun registerLifecycleObserver(observer: ServiceLifecycleObserver) {
        lifecycleObservers.add(observer)
    }

    fun notifyLifecycleStateOnCreate() {
        lifecycleObservers.forEach { it.onCreate() }
    }

    fun notifyLifecycleStateOnDestroy() {
        lifecycleObservers.forEach { it.onDestroy() }
        lifecycleObservers.clear()
    }
}