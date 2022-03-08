package tw.dcard.bubblemock.model

import android.util.Log
import okhttp3.Request

/**
 * @author Batu
 */
data class MockApi(
    private val urlSpec: UrlSpec
) {

    companion object {
        const val ANY_PATH = "_AnY_pAtH_"
        const val ANY_PARAM_VALUE = "_aNy_PaRaM_vAlUe"
    }

    var htmlMethod: String = "GET"

    var delay: Long = 0L
    var responseObject: Any? = null

    fun handle(request: Request): Any? {
        val url = request.url
        Log.d("badu", "url: $url")
        val segments = url.pathSegments

        if (responseObject == null) {
            return null
        }

        if (request.method != htmlMethod) {
            return null
        }

        when (urlSpec) {
            is UrlSpec.Detail -> {
                if (segments.size != urlSpec.urlSpecs.size) {
                    return null
                }

                urlSpec.urlSpecs.forEachIndexed { index, spec ->
                    if (spec != ANY_PATH && spec != segments[index]) {
                        return null
                    }
                }

                if (url.querySize != urlSpec.urlParams.size) {
                    return null
                }

                urlSpec.urlParams.forEach {
                    val realValue = url.queryParameter(it.key)
                    val mockValue = it.value
                    if (mockValue == ANY_PARAM_VALUE && realValue != null) {
                        return@forEach
                    }
                    if (realValue != mockValue) {
                        return null
                    }
                }
            }
            is UrlSpec.Regex -> {
                if (urlSpec.isContainsMatched) {
                    if (urlSpec.rules.toRegex().containsMatchIn(url.toString()).not())
                        return null

                } else {
                    if (urlSpec.rules.toRegex().matches(url.toString()).not())
                        return null
                }
            }
        }

        return responseObject
    }

    class Params : LinkedHashMap<String, String?>() {

        fun param(key: String, value: String?) {
            put(key, value)
        }
    }

    fun params(init: Params.() -> Unit) {
        val params = Params()
        params.init()
        (urlSpec as? UrlSpec.Detail)?.urlParams?.putAll(params)
    }

    fun response(block: () -> Any) {
        responseObject = block.invoke()
    }

    fun method(block: () -> String) {
        htmlMethod = block.invoke()
    }

    fun delay(block: () -> Long) {
        val _delay = block.invoke()
        delay = if (_delay < 0L) 0L else _delay
    }
}

fun apiDetail(vararg urlSpecs: String, init: MockApi.() -> Unit = {}): MockApi {
    val list = mutableListOf<String>().apply {
        for (urlSpec in urlSpecs) {
            add(urlSpec)
        }
    }
    val api = MockApi(UrlSpec.Detail(list))
    api.init()
    return api
}

fun apiDetail(urlSpecs: List<String>, init: MockApi.() -> Unit = {}): MockApi {
    val api = MockApi(UrlSpec.Detail(urlSpecs))
    api.init()
    return api
}

fun apiRegex(
    rule: String,
    isContainsMatched: Boolean = true,
    init: MockApi.() -> Unit = {}
): MockApi {
    val api = MockApi(UrlSpec.Regex(rule, isContainsMatched))
    api.init()
    return api
}