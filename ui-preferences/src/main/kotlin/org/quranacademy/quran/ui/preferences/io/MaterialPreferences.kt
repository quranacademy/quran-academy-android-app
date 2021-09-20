package org.quranacademy.quran.ui.preferences.io

import android.content.Context

class MaterialPreferences private constructor() {

    private var userInputModuleFactory: UserInputModule.Factory? = null
    private var storageModuleFactory: StorageModule.Factory? = null

    init {
        userInputModuleFactory = StandardUserInputFactory()
        storageModuleFactory = SharedPrefsStorageFactory(null)
    }

    fun setUserInputModule(factory: UserInputModule.Factory): MaterialPreferences {
        userInputModuleFactory = factory
        return this
    }

    fun setStorageModule(factory: StorageModule.Factory): MaterialPreferences {
        storageModuleFactory = factory
        return this
    }

    fun setDefault() {
        userInputModuleFactory = StandardUserInputFactory()
        storageModuleFactory = SharedPrefsStorageFactory(null)
    }

    private class StandardUserInputFactory : UserInputModule.Factory {

        override fun create(context: Context): UserInputModule {
            return StandardUserInputModule(context)
        }

    }

    companion object {

        private val instance = MaterialPreferences()

        fun instance(): MaterialPreferences {
            return instance
        }

        fun getUserInputModule(context: Context): UserInputModule {
            return instance.userInputModuleFactory!!.create(context)
        }

        fun getStorageModule(context: Context): StorageModule {
            return instance.storageModuleFactory!!.create(context)
        }
    }
}
