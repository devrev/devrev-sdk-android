package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = { TopAppBar(title = { Text("Web View") }) }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = {
                WebView(context).apply {
                    // This is required to enable JavaScript in the WebView
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true

                    // Load a local HTML file from the raw resources
                    loadUrl("file:///android_res/raw/sample.html")
                }
            }
        )
    }
}

class WebViewFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    WebViewScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.trackScreen("Web View")

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
