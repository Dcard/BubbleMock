package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.model.MockApi
import tw.dcard.bubblemock.model.MockRequest
import tw.dcard.bubblemock.model.api
import tw.dcard.bubblemock.model.request

object MemberMockRequest2 {

    fun create(): List<MockRequest> = listOf(
        //  DSL Declaration Method
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
        },
        //  Regular Declaration Method
        MockRequest(page = "Main Page", name = "Member List - same data with address").apply {
            mockApiList = mutableListOf(
                MockApi(listOf("members")).apply {
                    responseObject = getMembersWithAddress()
                }
            )
        }
    )

    private fun getMembers(): String =
        "[{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"age\":12,\"id\":132,\"name\":\"Belly World\"}]"

    private fun getMembersWithAddress(): String =
        "[{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"},{\"address\":\"No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan\",\"age\":12,\"id\":132,\"name\":\"Belly World\"}]\n"
}