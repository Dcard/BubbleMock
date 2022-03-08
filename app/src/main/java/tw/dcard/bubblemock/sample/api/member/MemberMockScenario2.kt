package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.model.*

object MemberMockScenario2 {

    fun create(): List<MockScenario> = listOf(
        //  DSL Declaration Method
        scenario(page = "Main Page", name = "Member List - same data") {
            select {
                false
            }
            add {
                apiDetail("members") {
                    response {
                        getMembers()
                    }
                }
            }
        },
        //  Regular Declaration Method
        MockScenario(page = "Main Page", name = "Member List - same data with address").apply {
            mockApiList = mutableListOf(
                MockApi(UrlSpec.Detail(listOf("members"))).apply {
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