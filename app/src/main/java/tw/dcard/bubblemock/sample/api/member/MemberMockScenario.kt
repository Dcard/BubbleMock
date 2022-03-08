package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.model.MockScenario
import tw.dcard.bubblemock.model.apiDetail
import tw.dcard.bubblemock.model.apiRegex
import tw.dcard.bubblemock.model.scenario
import tw.dcard.bubblemock.module.MockBubbleManager
import tw.dcard.bubblemock.sample.model.Member

object MemberMockScenario {
    fun create(): List<MockScenario> = listOf(
        scenario(page = "Main Page", name = "Member List - several") {
            select {
                true
            }
            add {
                apiRegex("members") {
                    response {
                        getMembers(5)
                    }
                }
            }
        },
        scenario(page = "Main Page", name = "Member List - lots data with long delay") {
            add {
                apiDetail("members") {
                    response {
                        getMembers(20)
                    }
                    delay {
                        3000L
                    }
                }
            }
        },
        scenario(page = "Main Page", name = "Member List - empty") {
            add {
                apiDetail("members") {
                    response {
                        getMembers(0)
                    }
                }
            }
        },
        scenario(page = "Main Page", name = "Member List - Error") {
            add {
                apiDetail("members") {
                    response {
                        MockBubbleManager.RESPONSE_ERROR
                    }
                }
            }
        }
    )

    private fun getMembers(count: Int) = mutableListOf<Member>().apply {
        for (i in 0 until count) {
            add(memberSample[i % memberSample.size])
        }
    }

    private val memberSample = listOf(
        Member(
            id = 1920,
            name = "Batu Tasvaluan",
            age = 31,
            address = "No. 7, Sec. 5, Xinyi Rd., Xinyi Dist., Taipei City 110, Taiwan"
        ),
        Member(
            id = 2431,
            name = "Savugan",
            age = 30,
            address = "2-chōme-6 Daiba, Minato City, Tōkyō-to"
        ),
        Member(
            id = 5244,
            name = "Lulu",
            age = 1,
            address = "328 Swanston St, Melbourne VIC 3000"
        ),
        Member(
            id = 41,
            name = "Cathy",
            age = 18,
            address = "Orlando, FL, USA"
        ),
        Member(
            id = 41,
            name = "Tiang",
            age = 24,
            address = "1100 Congress Ave, Austin, TX 78701"
        )
    )

}