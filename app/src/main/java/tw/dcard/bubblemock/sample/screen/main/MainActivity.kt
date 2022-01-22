package tw.dcard.bubblemock.sample.screen.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import tw.dcard.bubblemock.module.MockBubbleManager
import tw.dcard.bubblemock.sample.R
import tw.dcard.bubblemock.sample.model.Member
import tw.dcard.bubblemock.sample.module.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupRecyclerView()

        if (savedInstanceState == null) {
            /*  You can control the interceptor by your own local project config.   */
            // MockBubbleManager.getInstance().launchBubble(activity = this, isEnable = BuildConfig.ENABLE_MOCK)
            MockBubbleManager.getInstance().launchBubble(activity = this, isEnable = true)

            refresh()
        }
    }

    private fun setupViewModel() {
        viewModel.apply {
            members.observe(this@MainActivity, Observer {
                populateMembers(it)
                swipeRefreshLayout.isRefreshing = false
            })

            fetchMembersFailed.observe(this@MainActivity, Observer { liveEvent ->
                liveEvent?.consume()?.let { throwable ->
                    Toast.makeText(this@MainActivity, throwable.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun setupRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!)
            setHasFixedSize(true)
            adapter = MainAdapter()
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun refresh() {
        populateMembers(null)
        viewModel.getMemberInfo()
    }

    private fun populateMembers(members: List<Member>?) {
        val throwable = viewModel.fetchMembersFailed.value?.peek()
        val items = mutableListOf<MainAdapter.Item>().apply {
            if (members != null) {
                if (members.isNotEmpty()) {
                    addAll(members.map { MainAdapter.MemberItem(it) })
                    add(MainAdapter.TextItem("No more data."))
                } else {
                    add(MainAdapter.TextItem("No data."))
                }
            } else {
                if (throwable != null) {
                    add(MainAdapter.TextItem(throwable.message?.let { "Error: $it" } ?: "Error!"))
                }
            }
        }

        (recyclerView.adapter as MainAdapter).items = items
    }
}
