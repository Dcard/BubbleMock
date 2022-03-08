package tw.dcard.bubblemock.model

/**
 * @author Batu
 */
sealed class UrlSpec{
    data class Detail(
        val urlSpecs: List<String>,
        var urlParams: MutableMap<String, String?> = mutableMapOf(),
    ): UrlSpec()

    data class Regex(
        val rules: String,
        val isContainsMatched: Boolean = true,
    ): UrlSpec()
}
