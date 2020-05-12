package tw.dcard.bubblemock.model

/**
 * @author Batu
 */
data class MockScenario(
    val page: String,
    val name: String
) {
    var mockApiList = mutableListOf<MockApi>()
    var selected = false

    fun add(block: () -> MockApi) {
        mockApiList.add(block.invoke())
    }
    fun select(block: () -> Boolean){
        selected = block.invoke()
    }
}

fun scenario(page: String, name: String, init: MockScenario.() -> Unit): MockScenario {
    val mockRequest = MockScenario(page, name)
    mockRequest.init()
    return mockRequest
}