package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.model.MockRequest
import tw.dcard.bubblemock.model.api
import tw.dcard.bubblemock.model.request
import tw.dcard.bubblemock.sample.model.Member

object MemberMockRequest {
    fun create(): List<MockRequest> = listOf(
        request(page = "Main Page", name = "Member List") {
            select {
                true
            }
            add {
                api("members") {
                    response {
                        getMembers()
                    }
                }
            }
        }
    )

    private fun getMembers(): List<Member> = listOf(
        Member(
            id = 1920,
            name = "Batu Tasvaluan",
            age = 31,
            address = null
        ),
        Member(
            id = 2431,
            name = "Savugan",
            age = 1,
            address = null
        ),
        Member(
            id = 5244,
            name = "Langui Tasvaluan",
            age = 30,
            address = null
        )
    )

}