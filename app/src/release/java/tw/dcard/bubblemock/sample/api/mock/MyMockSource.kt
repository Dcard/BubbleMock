package tw.dcard.bubblemock.sample.api.mock

import tw.dcard.bubblemock.model.MockScenario
import tw.dcard.bubblemock.module.MockSource

class MyMockSource : MockSource {
    override fun create(): List<MockScenario> = listOf()
}