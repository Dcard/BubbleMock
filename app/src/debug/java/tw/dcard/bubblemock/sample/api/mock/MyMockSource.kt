package tw.dcard.bubblemock.sample.api.mock

import tw.dcard.bubblemock.model.MockScenario
import tw.dcard.bubblemock.module.MockSource
import tw.dcard.bubblemock.sample.api.member.MemberMockScenario
import tw.dcard.bubblemock.sample.api.member.MemberMockScenario2

class MyMockSource : MockSource {
    override fun create(): List<MockScenario> =
        mutableListOf<MockScenario>().apply {
            addAll(MemberMockScenario.create())
            addAll(MemberMockScenario2.create())
        }
}