package tw.dcard.bubblemock.sample

import android.app.Application
import tw.dcard.bubblemock.module.MockBubbleManager
import tw.dcard.bubblemock.sample.api.mock.MyMockSource

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MockBubbleManager.init(MyMockSource())
    }

}