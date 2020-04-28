package tw.dcard.bubblemock.sample.api.member

import tw.dcard.bubblemock.sample.model.Member

class RemoteMemberRepository(
    private val memberService: MemberService
) {
    suspend fun getMembers(): List<Member> = memberService.getMembers()

    suspend fun getMember(id: Long): Member = memberService.getMember(id)
}