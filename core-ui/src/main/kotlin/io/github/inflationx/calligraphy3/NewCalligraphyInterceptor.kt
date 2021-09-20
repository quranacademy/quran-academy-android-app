package io.github.inflationx.calligraphy3

import dev.b3nedikt.viewpump.InflateResult
import dev.b3nedikt.viewpump.Interceptor

class NewCalligraphyInterceptor(
        calligraphyConfig: CalligraphyConfig?
) : Interceptor {

    private val calligraphy = Calligraphy(calligraphyConfig)

    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result = chain.proceed(chain.request())
        val viewWithTypeface = calligraphy.onViewCreated(result.view, result.context, result.attrs)
        return InflateResult(
                view = viewWithTypeface,
                name = result.name,
                context = result.context,
                attrs = result.attrs
        )
    }

}