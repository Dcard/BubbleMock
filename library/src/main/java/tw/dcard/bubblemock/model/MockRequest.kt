package tw.dcard.bubblemock.model

/**
 * @author Batu
 */
data class MockRequest(
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

fun request(page: String, name: String, init: MockRequest.() -> Unit): MockRequest {
    val mockRequest = MockRequest(page, name)
    mockRequest.init()
    return mockRequest
}