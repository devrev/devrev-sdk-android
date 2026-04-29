package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.RealTimeUpdatesViewModel
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import ai.devrev.sdk.sample.viewmodel.StockData
import ai.devrev.sdk.sample.viewmodel.WebSocketStatus
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import java.util.Locale

private const val SELECTED_INDICATOR = "●"

@Composable
fun RealTimeUpdatesScreen(
    viewModel: RealTimeUpdatesViewModel,
    sharedViewModel: SharedViewModel
) {
    val stockData by viewModel.stockData.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val webSocketStatus by viewModel.webSocketStatus.collectAsState()
    val updateCount by viewModel.updateCount.collectAsState()
    val selectedStock by viewModel.selectedStock.collectAsState()
    val lastUpdatedStock by viewModel.lastUpdatedStock.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val screenTitle = stringResource(R.string.real_time_updates_screen)
    LaunchedEffect(Unit) {
        sharedViewModel.changeTitle(screenTitle)
        viewModel.startStreaming()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                StatusCard(
                    status = webSocketStatus,
                    updateCount = updateCount
                )
            }

            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFC62828),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (selectedStock != null && chartData.isNotEmpty()) {
                item {
                    ChartCard(
                        chartData = chartData,
                        selectedStock = selectedStock
                    )
                }
            } else if (webSocketStatus == WebSocketStatus.CONNECTED) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.tap_stock_to_view_chart),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.stock_monitor),
                    fontSize = 12.sp,
                    color = Color(0xFF454141),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

            items(stockData, key = { it.symbol }) { stock ->
                StockCard(
                    stock = stock,
                    isSelected = stock.symbol == selectedStock,
                    isUpdating = stock.symbol == lastUpdatedStock,
                    onClick = {
                        viewModel.selectStock(
                            if (selectedStock == stock.symbol) null else stock.symbol
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    status: WebSocketStatus,
    updateCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.websocket_status),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = when (status) {
                        WebSocketStatus.CONNECTED -> stringResource(R.string.connected)
                        WebSocketStatus.CONNECTING -> stringResource(R.string.connecting)
                        WebSocketStatus.DISCONNECTED -> stringResource(R.string.disconnected)
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (status) {
                        WebSocketStatus.CONNECTED -> Color(0xFF4CAF50)
                        WebSocketStatus.CONNECTING -> Color(0xFFFF9800)
                        WebSocketStatus.DISCONNECTED -> Color(0xFFF44336)
                    }
                )
            }

            Text(
                text = stringResource(R.string.updates_count, updateCount),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ChartCard(
    chartData: List<ai.devrev.sdk.sample.viewmodel.ChartPoint>,
    selectedStock: String?
) {
    if (chartData.isEmpty() || selectedStock == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.chart_title_last_updates, selectedStock, chartData.size),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val (normalizedData, minPrice, priceRange) = remember(chartData) {
                val min = chartData.minOf { it.y }
                val max = chartData.maxOf { it.y }
                val range = max - min
                val entries = chartData.mapIndexed { index, point ->
                    val normalizedY = if (range > 0) {
                        ((point.y - min) / range) * 100f
                    } else {
                        50f
                    }
                    FloatEntry(index.toFloat(), normalizedY)
                }
                Triple(entries, min, range)
            }

            val chartEntryModelProducer = remember { ChartEntryModelProducer(normalizedData) }

            LaunchedEffect(normalizedData) {
                chartEntryModelProducer.setEntries(normalizedData)
            }

            Chart(
                chart = lineChart(),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(
                    valueFormatter = { value, _ ->
                        val actualPrice = minPrice + (value / 100f) * priceRange
                        String.format(Locale.US, "$%.2f", actualPrice)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
fun StockCard(
    stock: StockData,
    isSelected: Boolean,
    isUpdating: Boolean,
    onClick: () -> Unit
) {
    val animatedPrice by animateFloatAsState(
        targetValue = stock.price.toFloat(),
        animationSpec = tween(durationMillis = 200),
        label = "price"
    )

    val pulseScale by animateFloatAsState(
        targetValue = if (isUpdating) 1.05f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "pulse"
    )

    val backgroundColor = when {
        isUpdating && stock.change > 0 -> Color(0xFFE8F5E9)
        isUpdating && stock.change < 0 -> Color(0xFFFFEBEE)
        isSelected -> Color(0xFFE3F2FD)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.symbol,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = SELECTED_INDICATOR,
                            fontSize = 12.sp,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
                if (stock.price == 0.0) {
                    Text(
                        text = stringResource(R.string.waiting_for_data),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                } else {
                    Text(
                        text = stringResource(R.string.price_format, animatedPrice),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (stock.price != 0.0) {
                    Text(
                        text = "${if (stock.change >= 0) "+" else ""}${String.format(Locale.US, "%.2f", stock.change)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (stock.change >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Text(
                        text = "${if (stock.changePercent >= 0) "+" else ""}${String.format(Locale.US, "%.2f", stock.changePercent)}%",
                        fontSize = 14.sp,
                        color = if (stock.changePercent >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

class RealTimeUpdatesFragment : Fragment() {
    private val viewModel: RealTimeUpdatesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    RealTimeUpdatesScreen(
                        viewModel = viewModel,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.trackScreen("Real-time Updates")

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopStreaming()
    }
}
