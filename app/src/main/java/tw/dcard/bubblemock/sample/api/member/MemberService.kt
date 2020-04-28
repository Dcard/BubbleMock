package tw.dcard.bubblemock.sample.api.member

import retrofit2.http.GET
import tw.dcard.bubblemock.sample.model.Member

interface MemberService {

    @GET("members")
    suspend fun getMembers(): List<Member>

    @GET("member/{id}")
    suspend fun getMember(id: Long): Member
}