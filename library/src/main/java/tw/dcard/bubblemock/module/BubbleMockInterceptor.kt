package tw.dcard.bubblemock.module

import okhttp3.Interceptor
import okhttp3.Response

class BubbleMockInterceptor(private val isEnable: Boolean = false) : Interceptor {

    private val bubbleMock = MockBubbleManager.getInstance()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return if (isEnable) {
            bubbleMock.handle(request) ?: chain.proceed(request)
        } else {
            chain.proceed(request)
        }
    }
}