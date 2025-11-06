package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.adapter.RecyclerViewListAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ai.devrev.sdk.sample.viewmodel.RecyclerItemsViewModel
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import androidx.fragment.app.viewModels

class RecyclerViewListFragment : Fragment() {

    private val viewModel: RecyclerItemsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recyclerView = RecyclerView(requireContext())
        val padding = (8 * requireContext().resources.displayMetrics.density).toInt()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe items from ViewModel
        viewModel.items.observe(viewLifecycleOwner) { items ->
            recyclerView.adapter = RecyclerViewListAdapter(items) { cardView, position ->
                if (position % 2 == 0) {
                    viewModel.markSensitive(cardView)
                } else {
                    viewModel.unmarkSensitive(cardView)
                }
            }
        }

        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        recyclerView.setPadding(padding, padding, padding, padding)
        recyclerView.clipToPadding = false
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.trackScreen("Recycler View")

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }
}
