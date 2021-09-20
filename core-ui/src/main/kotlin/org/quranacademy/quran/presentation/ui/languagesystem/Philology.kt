package org.quranacademy.quran.presentation.ui.languagesystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import org.quranacademy.quran.core.ui.R
import org.quranacademy.quran.presentation.ui.languagesystem.android.PhilologyContextWrapper
import org.quranacademy.quran.presentation.ui.languagesystem.repository.EmptyPhilologyRepository
import org.quranacademy.quran.presentation.ui.languagesystem.repository.HQAPhilologyRepositoryFactory
import org.quranacademy.quran.presentation.ui.languagesystem.repository.PhilologyRepository
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.ViewTransformer
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories.EmptyViewTransformerFactory
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories.InternalViewTransformerFactory
import org.quranacademy.quran.presentation.ui.languagesystem.transformers.factories.ViewTransformerFactory

/**
 * Phylology is custom language system based on Phylology:
 *
 * https://github.com/JcMinarro/Philology
 */
@SuppressLint("StaticFieldLeak")
object Philology {

    private lateinit var originalAppContext: Context
    private lateinit var currentLanguageRepository: PhilologyRepository
    private val originalStringsIds = mutableMapOf<String, Int>()
    private val repositoryMap = mutableMapOf<String, PhilologyRepository>()
    private var repositoryFactory = HQAPhilologyRepositoryFactory
    private var viewTransformerFactory: ViewTransformerFactory = EmptyViewTransformerFactory

    fun setOriginalAppContext(context: Context) {
        originalAppContext = context
    }

    @JvmOverloads
    fun init(
            languageCode: String,
            viewTransformerFactory: ViewTransformerFactory = EmptyViewTransformerFactory
    ) {
        this.viewTransformerFactory = viewTransformerFactory
        this.currentLanguageRepository = getPhilologyRepository(languageCode)

        loadOriginalStrings()
    }

    fun wrap(baseContext: Context): ContextWrapper = PhilologyContextWrapper(baseContext)

    fun getPhilologyRepository(languageCode: String): PhilologyRepository {
        return repositoryMap.getOrPut(languageCode) {
            repositoryFactory.getPhilologyRepository(languageCode)
        }
    }

    internal fun getCurrentPhilologyRepository(): PhilologyRepository {
        return if (::currentLanguageRepository.isInitialized) {
            currentLanguageRepository
        } else EmptyPhilologyRepository
    }

    internal fun getViewTransformer(view: View): ViewTransformer {
        val externalViewTransformer = viewTransformerFactory.getViewTransformer(view)
        return externalViewTransformer ?: InternalViewTransformerFactory.getViewTransformer(view)
    }

    fun getStringId(text: String): Int? = originalStringsIds[text]

    fun getOriginalContext(): Context = originalAppContext

    fun setLanguage(languageCode: String) {
        currentLanguageRepository = getPhilologyRepository(languageCode)
    }

    private fun loadOriginalStrings() {
        val fields = R.string::class.java.declaredFields // or Field[] fields = R.string.class.getFields();
        for (i in fields.indices) {
            val fieldName = fields[i].name
            val resId = originalAppContext.resources.getIdentifier(fieldName, "string", originalAppContext.packageName)
            if (resId != 0) {
                val value = originalAppContext.resources.getString(resId)
                originalStringsIds[value] = resId
            }
        }
    }

}

