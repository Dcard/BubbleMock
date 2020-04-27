package tw.dcard.bubblemock.sample

import android.annotation.SuppressLint
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tw.dcard.bubblemock.sample.api.ApiStation
import tw.dcard.bubblemock.sample.api.member.RemoteMemberRepository

class ViewModelFactory() : ViewModelProvider.NewInstanceFactory() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance() = INSTANCE ?: synchronized(ViewModelFactory::class.java) {
            INSTANCE ?: ViewModelFactory(
            ).also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private val apiStation = ApiStation.getInstance()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {

                isAssignableFrom(MainViewModel::class.java) -> {
                    val remoteMemberRepository = RemoteMemberRepository(apiStation.memberService)
                    MainViewModel(remoteMemberRepository)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }

}