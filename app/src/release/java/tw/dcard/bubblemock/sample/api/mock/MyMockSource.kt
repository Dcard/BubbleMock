package tw.dcard.bubblemock.sample.api.mock

import tw.dcard.bubblemock.model.MockRequest
import tw.dcard.bubblemock.module.MockSource

class MyMockSource : MockSource {
    override fun create(): List<MockRequest> = list()
}