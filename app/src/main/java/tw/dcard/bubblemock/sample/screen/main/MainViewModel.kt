package tw.dcard.bubblemock.sample.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tw.dcard.bubblemock.sample.api.member.RemoteMemberRepository
import tw.dcard.bubblemock.sample.model.Member
import tw.dcard.bubblemock.sample.module.LiveEvent

class MainViewModel(
    private val memberSource: RemoteMemberRepository
) : ViewModel() {

    val members = MutableLiveData<List<Member>?>()
    val fetchMembersFailed = MutableLiveData<LiveEvent<Throwable>?>()

    fun getMemberInfo() {
        viewModelScope.launch {
            fetchMembersFailed.value = null
            delay(1000)
            members.value = try {
                memberSource.getMembers()
            } catch (t: Throwable) {
                fetchMembersFailed.value = LiveEvent(t)
                null
            }
        }
    }
}