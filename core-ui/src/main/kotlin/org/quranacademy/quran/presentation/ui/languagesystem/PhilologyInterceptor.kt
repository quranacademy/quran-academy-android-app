package org.quranacademy.quran.presentation.ui.languagesystem

import android.util.AttributeSet
import android.view.View
import dev.b3nedikt.viewpump.InflateResult
import dev.b3nedikt.viewpump.Interceptor

object PhilologyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): InflateResult =
            chain.proceed(chain.request()).let { inflateResult ->
                rewordView(inflateResult.view, inflateResult.attrs)
                inflateResult
            }

    private fun rewordView(view: View?, attributeSet: AttributeSet?): View? {
        if (view != null && attributeSet != null) {
            val viewTransformer = Philology.getViewTransformer(view)
            viewTransformer.reword(view, attributeSet)
        }
        return view
    }

}