package ai.devrev.sdk.sample

import ai.devrev.sdk.observability_compose.markAsMaskedLocation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListViewScreen(items: List<String>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Large Scrollable List (Jetpack Compose)") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(items) { index, item ->
                // We are masking every alternate item in the list
                Card(
                    modifier = (if (index % 2 == 0) {
                        Modifier.markAsMaskedLocation(index.toString())
                    } else {
                        Modifier
                    })
                        .fillParentMaxWidth()
                        .padding(8.dp)

                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

class ComposeViewListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ListViewScreen(
                        items = List(100) { "Item #$it" }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
