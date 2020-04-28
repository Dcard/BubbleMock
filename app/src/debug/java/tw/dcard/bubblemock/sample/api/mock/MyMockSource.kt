package tw.dcard.bubblemock.sample.api.mock

import tw.dcard.bubblemock.model.MockRequest
import tw.dcard.bubblemock.module.MockSource
import tw.dcard.bubblemock.sample.api.member.MemberMockRequest

class MyMockSource : MockSource {
    override fun create(): List<MockRequest> =
        mutableListOf<MockRequest>().apply {
            addAll(MemberMockRequest.create())
        }
}