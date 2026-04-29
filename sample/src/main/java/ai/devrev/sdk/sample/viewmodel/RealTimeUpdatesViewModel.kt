package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.trackEvent
import ai.devrev.sdk.sample.R
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.util.Log
import java.util.concurrent.TimeUnit

data class StockData(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val timestamp: Long
)

data class ChartPoint(
    val y: Float
)

enum class WebSocketStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

data class BinanceStreamResponse(
    val stream: String,
    val data: BinanceTrade
)

data class BinanceTrade(
    @SerializedName("e") val eventType: String,
    @SerializedName("E") val eventTime: Long,
    @SerializedName("s") val symbol: String,
    @SerializedName("t") val tradeId: Long,
    @SerializedName("p") val price: String,
    @SerializedName("q") val quantity: String,
    @SerializedName("T") val tradeTime: Long
)

class RealTimeUpdatesViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "RealTimeUpdatesViewModel"
        private const val MAX_CHART_POINTS = 30
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val RECONNECT_DELAY_MS = 3000L
        private const val WEBSOCKET_NORMAL_CLOSURE = 1000
        private const val WEBSOCKET_CLOSE_REASON = "User Stopped Streaming"
        private const val LAST_UPDATE_INDICATOR_DELAY_MS = 300L
    }

    private val _stockData = MutableStateFlow<List<StockData>>(emptyList())
    val stockData: StateFlow<List<StockData>> = _stockData.asStateFlow()
    private val _chartData = MutableStateFlow<List<ChartPoint>>(emptyList())
    val chartData: StateFlow<List<ChartPoint>> = _chartData.asStateFlow()
    private val _webSocketStatus = MutableStateFlow(WebSocketStatus.DISCONNECTED)
    val webSocketStatus: StateFlow<WebSocketStatus> = _webSocketStatus.asStateFlow()
    private val _updateCount = MutableStateFlow(0)
    val updateCount: StateFlow<Int> = _updateCount.asStateFlow()
    private val _selectedStock = MutableStateFlow<String?>(null)
    val selectedStock: StateFlow<String?> = _selectedStock.asStateFlow()
    private val _lastUpdatedStock = MutableStateFlow<String?>(null)
    val lastUpdatedStock: StateFlow<String?> = _lastUpdatedStock.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    @Volatile private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(20, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val reconnectAttempts = AtomicInteger(0)
    @Volatile private var isManualDisconnect = false
    private val stockSymbols = listOf(
        "BTCUSDT",
        "ETHUSDT",
        "BNBUSDT",
        "ADAUSDT",
        "SOLUSDT",
        "XRPUSDT",
        "DOGEUSDT",
        "DOTUSDT",
        "MATICUSDT",
        "LINKUSDT",
        "AVAXUSDT",
        "UNIUSDT",
        "ATOMUSDT",
        "LTCUSDT",
        "TRXUSDT"
    )
    private val webSocketUrl = "wss://stream.binance.com:9443/stream?streams=${stockSymbols.joinToString("/") { symbol -> "${symbol.lowercase()}@trade" }}"
    private val lastPrices = ConcurrentHashMap<String, Double>()
    private val stockPriceHistories = ConcurrentHashMap<String, MutableList<ChartPoint>>()
    private val stockIndexMap = ConcurrentHashMap<String, Int>()
    private val lastUpdateJobs = ConcurrentHashMap<String, Job>()
    init {
        val initialStocks = stockSymbols.mapIndexed { index, symbol ->
            stockPriceHistories[symbol] = Collections.synchronizedList(mutableListOf())
            stockIndexMap[symbol] = index
            StockData(
                symbol = symbol,
                price = 0.0,
                change = 0.0,
                changePercent = 0.0,
                timestamp = System.currentTimeMillis()
            )
        }
        _stockData.value = initialStocks
    }
    fun selectStock(symbol: String?) {
        _selectedStock.value = symbol
        val history = if (symbol != null) stockPriceHistories[symbol] else null
        _chartData.value = if (history != null) synchronized(history) { history.toList() } else emptyList()
    }
    fun startStreaming() {
        if (webSocket != null) {
            return
        }
        isManualDisconnect = false
        reconnectAttempts.set(0)
        _errorMessage.value = null
        connectWebSocket()
    }

    private fun connectWebSocket() {
        _webSocketStatus.value = WebSocketStatus.CONNECTING
        try {
            DevRev.trackEvent("websocket_start", hashMapOf(
                "status" to "connecting",
                "timestamp" to System.currentTimeMillis().toString(),
                "api" to "binance"
            ))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to track websocket_start: ${e.message}")
        }
        val request = Request.Builder().url(webSocketUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _webSocketStatus.value = WebSocketStatus.CONNECTED
                reconnectAttempts.set(0)
                _errorMessage.value = null
                try {
                    DevRev.trackEvent("websocket_connected", hashMapOf(
                        "status" to "connected",
                        "timestamp" to System.currentTimeMillis().toString()
                    ))
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to track websocket_connected: ${e.message}")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val response = gson.fromJson(text, BinanceStreamResponse::class.java)
                    processTradeUpdate(response.data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing message: ${e.message}")
                    try {
                        DevRev.trackEvent("websocket_parse_error", hashMapOf(
                            "error" to e.message.toString(),
                            "timestamp" to System.currentTimeMillis().toString()
                        ))
                    } catch (trackError: Exception) {
                        Log.w(TAG, "Failed to track parse error: ${trackError.message}")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                _webSocketStatus.value = WebSocketStatus.DISCONNECTED

                try {
                    DevRev.trackEvent("websocket_failure", hashMapOf(
                        "error" to t.message.toString(),
                        "timestamp" to System.currentTimeMillis().toString(),
                        "reconnect_attempt" to reconnectAttempts.get().toString()
                    ))
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to track websocket_failure: ${e.message}")
                }

                if (!isManualDisconnect && reconnectAttempts.get() < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts.incrementAndGet()
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(RECONNECT_DELAY_MS)
                        if (!isManualDisconnect) {
                            this@RealTimeUpdatesViewModel.webSocket = null
                            connectWebSocket()
                        }
                    }
                } else {
                    if (!isManualDisconnect) {
                        _errorMessage.value = getApplication<Application>().getString(R.string.connection_failed_message, MAX_RECONNECT_ATTEMPTS)
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(WEBSOCKET_NORMAL_CLOSURE, null)
                _webSocketStatus.value = WebSocketStatus.DISCONNECTED
                try {
                    DevRev.trackEvent("websocket_closing", hashMapOf(
                        "code" to code.toString(),
                        "reason" to reason,
                        "timestamp" to System.currentTimeMillis().toString()
                    ))
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to track websocket_closing: ${e.message}")
                }
                if (!isManualDisconnect) {
                    reconnectAttempts.set(0)
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(RECONNECT_DELAY_MS)
                        if (!isManualDisconnect) {
                            this@RealTimeUpdatesViewModel.webSocket = null
                            connectWebSocket()
                        }
                    }
                }
            }
        })
    }
    private fun processTradeUpdate(trade: BinanceTrade) {
        viewModelScope.launch(Dispatchers.Default) {
            val price = trade.price.toDoubleOrNull() ?: return@launch
            var lastPrice: Double? = null
            lastPrices.compute(trade.symbol) { _, existing ->
                lastPrice = existing
                price
            }
            val change = if (lastPrice != null) price - lastPrice!! else 0.0
            val changePercent = if (lastPrice != null && lastPrice != 0.0) (change / lastPrice!!) * 100 else 0.0
            val stockData = StockData(
                symbol = trade.symbol,
                price = price,
                change = change,
                changePercent = changePercent,
                timestamp = trade.tradeTime
            )

            val history = stockPriceHistories.getOrPut(trade.symbol) { mutableListOf() }
            synchronized(history) {
                history.add(ChartPoint(price.toFloat()))
                if (history.size > MAX_CHART_POINTS) {
                    history.removeAt(0)
                }
            }

            val newChartData = if (_selectedStock.value == trade.symbol) {
                val hist = stockPriceHistories[trade.symbol]
                if (hist != null) synchronized(hist) { hist.toList() } else emptyList()
            } else {
                _chartData.value
            }

            withContext(Dispatchers.Main) {
                _stockData.update { currentList ->
                    val mutableList = currentList.toMutableList()
                    val existingIndex = stockIndexMap[trade.symbol]
                    if (existingIndex != null && existingIndex < mutableList.size) {
                        mutableList[existingIndex] = stockData
                    } else {
                        mutableList.add(stockData)
                    }
                    mutableList
                }
                _chartData.value = newChartData
                _updateCount.update { count -> count + 1 }
                _lastUpdatedStock.value = trade.symbol
            }

            lastUpdateJobs.compute(trade.symbol) { _, existingJob ->
                existingJob?.cancel()
                viewModelScope.launch(Dispatchers.Default) {
                    kotlinx.coroutines.delay(LAST_UPDATE_INDICATOR_DELAY_MS)
                    withContext(Dispatchers.Main) {
                        if (_lastUpdatedStock.value == trade.symbol) {
                            _lastUpdatedStock.value = null
                        }
                    }
                }
            }

            try {
                DevRev.trackEvent("stock_price_update", hashMapOf(
                    "symbol" to trade.symbol,
                    "price" to price.toString(),
                    "change" to change.toString(),
                    "change_percent" to changePercent.toString(),
                    "timestamp" to trade.tradeTime.toString()
                ))
            } catch (e: Exception) {
                Log.w(TAG, "Failed to track event: ${e.message}")
            }
        }
    }
    fun stopStreaming() {
        if (isManualDisconnect) return
        isManualDisconnect = true
        _errorMessage.value = null
        webSocket?.close(WEBSOCKET_NORMAL_CLOSURE, WEBSOCKET_CLOSE_REASON)
        webSocket = null
        _webSocketStatus.value = WebSocketStatus.DISCONNECTED
        try {
            DevRev.trackEvent("websocket_stop", hashMapOf(
                "total_updates" to _updateCount.value.toString(),
                "timestamp" to System.currentTimeMillis().toString()
            ))
        } catch (e: Exception) {
            Log.w(TAG, "Failed to track websocket_stop: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopStreaming()
        client.dispatcher.executorService.shutdown()
    }
}