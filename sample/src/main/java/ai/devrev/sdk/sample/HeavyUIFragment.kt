package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun HeavyUIScreen(sharedViewModel: SharedViewModel, onNext: () -> Unit) {
    val heavyUITitle = stringResource(R.string.heavy_ui_screen)
    LaunchedEffect(Unit) {
        sharedViewModel.changeTitle(heavyUITitle)
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.be_bold_animation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(progress) {
        if (progress == 1f && composition != null) {
            onNext()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .clickable { onNext() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.heavy_ui_next),
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.heavy_ui_next),
                tint = Color.White
            )
        }
    }
}

class HeavyUIFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    HeavyUIScreen(
                        sharedViewModel = sharedViewModel,
                        onNext = {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, HeavyUIContentFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setBackgroundColor(AndroidColor.BLACK)
        requireActivity().window?.decorView?.setBackgroundColor(AndroidColor.BLACK)

        sharedViewModel.trackScreen("HeavyUI")

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().window?.decorView?.setBackgroundColor(AndroidColor.WHITE)
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        requireActivity().window?.decorView?.setBackgroundColor(AndroidColor.WHITE)
        super.onDestroyView()
    }
}
