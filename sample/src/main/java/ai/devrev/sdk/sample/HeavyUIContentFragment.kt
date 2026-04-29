package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import android.view.SurfaceHolder
import android.view.SurfaceView
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.*

sealed class LottieSource {
    data class Url(val url: String) : LottieSource()
    data class RawRes(val resId: Int) : LottieSource()
}

@Composable
fun HeavyUIContentScreen(sharedViewModel: SharedViewModel) {
    val heavyUITitle = stringResource(R.string.heavy_ui_screen)
    LaunchedEffect(Unit) {
        sharedViewModel.changeTitle(heavyUITitle)
    }

    val lottieAnimationRows = listOf(
        listOf(
            LottieSource.Url("https://assets9.lottiefiles.com/packages/lf20_jbrw3hcz.json"),
            LottieSource.Url("https://assets1.lottiefiles.com/packages/lf20_touohxv0.json"),
            LottieSource.Url("https://assets4.lottiefiles.com/packages/lf20_lk80fpsm.json")
        ),
        listOf(
            LottieSource.RawRes(R.raw.developer_animation),
            LottieSource.Url("https://assets4.lottiefiles.com/packages/lf20_p8bfn5to.json"),
            LottieSource.Url("https://assets8.lottiefiles.com/packages/lf20_j3UXNf.json")
        ),
        listOf(
            LottieSource.RawRes(R.raw.say_hi),
            LottieSource.Url("https://assets2.lottiefiles.com/packages/lf20_uu0x8lqv.json"),
            LottieSource.Url("https://assets1.lottiefiles.com/packages/lf20_V9t630.json")
        ),
        listOf(
            LottieSource.Url("https://assets5.lottiefiles.com/packages/lf20_w98qte06.json"),
            LottieSource.Url("https://assets3.lottiefiles.com/packages/lf20_w51pcehl.json"),
            LottieSource.Url("https://assets2.lottiefiles.com/packages/lf20_rwq6ciql.json")
        )
    )

    val hdImages = listOf(
        "https://images.unsplash.com/photo-1555066931-4365d14bab8c",
        "https://images.unsplash.com/photo-1521737604893-d14cc237f11d",
        "https://images.unsplash.com/photo-1677442136019-21780ecad995",
        "https://images.unsplash.com/photo-1498050108023-c5249f4df085"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            VideoPlayerSection()
        }

        item {
            Text(
                text = stringResource(R.string.heavy_ui_animations),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            val horizontalScrollState = rememberScrollState()
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lottieAnimationRows.forEach { columnAnimations ->
                        LazyColumn(
                            modifier = Modifier
                                .width(200.dp)
                                .height(440.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(columnAnimations) { source ->
                                LottieAnimationItem(
                                    source = source,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .height(210.dp)
                                )
                            }
                        }
                    }
                }

                if (horizontalScrollState.isScrollInProgress) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(4.dp)
                            .background(Color.LightGray, RoundedCornerShape(2.dp))
                    ) {
                        val indicatorWidth = 0.25f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(indicatorWidth)
                                .fillMaxHeight()
                                .offset(x = (horizontalScrollState.value.toFloat() / horizontalScrollState.maxValue * (1f - indicatorWidth) * 300).dp)
                                .background(Color.DarkGray, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.heavy_ui_images),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hdImages) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "$imageUrl?w=1200&h=800&fit=crop&q=80"
                        ),
                        contentDescription = "Image",
                        modifier = Modifier
                            .width(350.dp)
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayerSection() {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_video}")
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            exoPlayer.setVideoSurfaceHolder(holder)
                        }
                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            exoPlayer.clearVideoSurfaceHolder(holder)
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LottieAnimationItem(source: LottieSource, modifier: Modifier = Modifier) {
    val compositionSpec = when (source) {
        is LottieSource.Url -> LottieCompositionSpec.Url(source.url)
        is LottieSource.RawRes -> LottieCompositionSpec.RawRes(source.resId)
    }

    val composition by rememberLottieComposition(compositionSpec)
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )
    }
}

class HeavyUIContentFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    HeavyUIContentScreen(sharedViewModel = sharedViewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.trackScreen("HeavyUIContent")
        sharedViewModel.setHeavyUIActive(true)

        requireActivity().window?.decorView?.setBackgroundColor(AndroidColor.WHITE)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    sharedViewModel.setHeavyUIActive(false)
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        sharedViewModel.setHeavyUIActive(false)
        super.onDestroyView()
    }
}
