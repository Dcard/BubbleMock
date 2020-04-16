package tw.dcard.bubblemock.model

import okhttp3.Request

/**
 * @author Batu
 */
data class MockApi(
    private val urlSpecs: List<String>
) {

    companion object {
        const val ANY_PATH = "_AnY_pAtH_"
        const val ANY_PARAM_VALUE = "_aNy_PaRaM_vAlUe"
    }

    var htmlMethod: String = "GET"
    var urlParams: MutableMap<String, String> = mutableMapOf()
    var responseObject: Any? = null

    fun handle(request: Request): Any? {
        val url = request.url()
        val segments = url.pathSegments()

        if (responseObject == null) {
            return null
        }

        if (request.method() != htmlMethod) {
            return null
        }

        if (segments.size != urlSpecs.size) {
            return null
        }

        urlSpecs.forEachIndexed { index, spec ->
            if (spec != ANY_PATH && spec != segments[index]) {
                return null
            }
        }

        if (url.querySize() != urlParams.size) {
            return null
        }

        urlParams.forEach {
            val realValue = url.queryParameter(it.key)
            val mockValue = it.value
            if (mockValue == ANY_PARAM_VALUE && realValue != null) {
                return@forEach
            }
            if (realValue != mockValue) {
                return null
            }
        }

        return responseObject
    }

    class Params : LinkedHashMap<String, String>() {

        fun param(key: String, value: String) {
            put(key, value)
        }
    }

    fun params(init: Params.() -> Unit) {
        val params = Params()
        params.init()
        urlParams.putAll(params)
    }

    fun response(block: () -> Any) {
        responseObject = block.invoke()
    }

    fun method(block: () -> String) {
        htmlMethod = block.invoke()
    }
}

fun api(vararg urlSpecs: String, init: MockApi.() -> Unit = {}): MockApi {
    val list = mutableListOf<String>().apply {
        for (urlSpec in urlSpecs) {
            add(urlSpec)
        }
    }
    val api = MockApi(list)
    api.init()
    return api
}

fun api(urlSpecs: List<String>, init: MockApi.() -> Unit = {}): MockApi {
    val api = MockApi(urlSpecs)
    api.init()
    return api
}