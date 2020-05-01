package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.model.MockRequest
import tw.dcard.bubblemock.model.api
import tw.dcard.bubblemock.model.request

object MemberMockRequest2 {

    fun create(): List<MockRequest> = listOf(
        request(page = "Main Page", name = "Member List - same data") {
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

    private fun getMembers(): String =
        "[{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"}]"
}