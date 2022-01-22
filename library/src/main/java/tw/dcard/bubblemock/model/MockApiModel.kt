package tw.dcard.bubblemock.model

import okhttp3.Request

/**
 * @author Batu
 */
data class MockApiModel(
    val id: Int,
    val urlSpec: List<String?>,
    val method: String = "GET",
    val params: Map<String, String>? = null
) {

    lateinit var responseObject: Any

    fun handle(request: Request): Any? {
        val url = request.url
        val segments = url.pathSegments

        if (segments.size != urlSpec.size) {
            return null
        }

        urlSpec.forEachIndexed { index, spec ->
            if (spec != null && spec != segments[index]) {
                return null
            }
        }

        if (request.method != method) {
            return null
        }

        params?.forEach {
            if (url.queryParameter(it.key) == null && it.value.isEmpty()) {
                return@forEach
            }
            if (url.queryParameter(it.key) != it.value) {
                return null
            }
        }

        return responseObject
    }
}