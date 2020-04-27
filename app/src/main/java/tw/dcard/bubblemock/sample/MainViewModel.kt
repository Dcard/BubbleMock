package tw.dcard.bubblemock.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tw.dcard.bubblemock.sample.api.member.RemoteMemberRepository
import tw.dcard.bubblemock.sample.model.Member

class MainViewModel(
    private val memberSource: RemoteMemberRepository
) : ViewModel() {

    val members = MutableLiveData<List<Member>?>()

    fun getMemberInfo() {
        viewModelScope.launch {
            members.value = try {
                memberSource.getMembers()
            } catch (t: Throwable) {
                null
            }
        }
    }
}