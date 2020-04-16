package tw.dcard.bubblemock.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_mock_bubble.*
import tw.dcard.bubblemock.R

/**
 * @author Batu
 */
class BubbleMainFragment : Fragment(), BubbleMainAdapter.Interaction {

    companion object {
        fun newInstance() = BubbleMainFragment()
    }

    private lateinit var viewModel: BubbleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BubbleViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mock_bubble, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        fetchData()
    }

    override fun onItemSelected(index: Int, selected: Boolean) {
        viewModel.selectMock(index, selected)
    }

    private fun setupViewModel() {
        viewModel.apply {
            mockModels.observe(viewLifecycleOwner, Observer {
                populateRecyclerView(it)
            })
        }
    }

    private fun populateRecyclerView(list: List<BubbleMainItem>) {
        (recyclerView.adapter as BubbleMainAdapter).items = list
    }

    private fun fetchData() {
        viewModel.fetchMock()
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = BubbleMainAdapter(this@BubbleMainFragment)
        }
    }
}