package tw.dcard.bubblemock.sample.screen.main

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import tw.dcard.bubblemock.Calculator
import tw.dcard.bubblemock.module.MockBubbleManager
import tw.dcard.bubblemock.sample.R
import tw.dcard.bubblemock.sample.module.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Calculator().add(1, 2)
        setupViewModel()

        if (savedInstanceState == null) {
            /*  You can control the interceptor by your own local project config.   */
            // MockBubbleManager.getInstance().launchBubble(activity = this, isEnable = BuildConfig.ENABLE_MOCK)
            MockBubbleManager.getInstance().launchBubble(activity = this, isEnable = true)
            Handler().postDelayed({
                viewModel.getMemberInfo()
            }, 1000)
        }
    }

    private fun setupViewModel() {
        viewModel.apply {
            members.observe(this@MainActivity, Observer {
                val message = "I got ${it?.size ?: 0} members"
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            })

            fetchMembersFailed.observe(this@MainActivity, Observer {
                it.getContent()?.let { throwable ->
                    Toast.makeText(this@MainActivity, throwable.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
