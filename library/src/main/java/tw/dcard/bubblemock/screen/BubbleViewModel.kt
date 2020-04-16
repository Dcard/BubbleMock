package tw.dcard.bubblemock.screen

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import tw.dcard.bubblemock.module.MockBubbleManager

/**
 * @author Batu
 */
class BubbleViewModel : ViewModel() {

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines
     * launched by uiScope by calling viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    @CallSuper
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    val mockModels = MutableLiveData<List<BubbleMainItem>>()

    fun fetchMock() {
        viewModelScope.launch {
            mockModels.value = withContext(Dispatchers.Default) {

                MockBubbleManager.getInstance().mockOperationModels?.map {
                    BubbleMainItem(
                        it.page, it.name, it.selected
                    )
                } ?: listOf()
            }
        }
    }

    fun selectMock(index: Int, selected: Boolean) {
        mockModels.value?.get(index)?.selected = selected
        MockBubbleManager.getInstance().mockOperationModels?.get(index)?.selected = selected
    }
}