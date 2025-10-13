package ai.devrev.sdk.sample

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.isMonitoringEnabled
import ai.devrev.sdk.sample.handler.NotificationHandler
import ai.devrev.sdk.sample.model.AppRoute
import ai.devrev.sdk.sample.viewmodel.SharedViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var notificationHandler: NotificationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHandler = NotificationHandler(this)
        handleNotificationIntent(intent)
        setContent {
            SampleApp()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.getBooleanExtra("notification_pressed", false) == true) {
            val message = intent.getStringExtra("message")
            notificationHandler.handleNotificationClick(message)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleApp(viewModel: SharedViewModel = viewModel()) {
    val title by viewModel.title.observeAsState(stringResource(R.string.devrev_sdk))
    val navController = rememberNavController()
    val context = LocalContext.current

    navController.currentBackStackEntry?.destination?.route?.let { route ->
        navController.navigate(route) {
            popUpTo(route) { inclusive = true }
            launchSingleTop = true
        }
    }
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Scaffold(
                modifier = Modifier.background(Color.White),
                containerColor = Color.White,
                topBar = {
                    val currentBackStackEntry = navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStackEntry.value?.destination
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = title, fontWeight = FontWeight.Bold)
                            }
                        },
                        navigationIcon = {
                            if (currentDestination?.route != AppRoute.HOME.route) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.size(48.dp))
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val currentRoute =
                                    navController.currentBackStackEntry?.destination?.route
                                if (currentRoute != null) {
                                    when (currentRoute) {
                                        AppRoute.HOME.route -> {
                                            navController.navigate(currentRoute) {
                                                popUpTo(currentRoute) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }

                                        else -> {
                                            val fragment = when (currentRoute) {
                                                AppRoute.IDENTIFICATION.route -> IdentificationFragment()
                                                AppRoute.SUPPORT_CHAT.route -> SupportChatFragment()
                                                AppRoute.PUSH_NOTIFICATIONS.route -> PushNotificationsFragment()
                                                AppRoute.SESSION_ANALYTICS.route -> SessionAnalyticsFragment()
                                                else -> null
                                            }
                                            if (fragment != null) {
                                                val fragmentManager =
                                                    (context as FragmentActivity).supportFragmentManager
                                                reloadFragment(
                                                    fragmentManager,
                                                    fragment,
                                                    R.id.fragment_container_view
                                                )
                                            }
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Gray
                        )
                    )
                }
            ) { paddingValues ->
                NavHostContainer(paddingValues, navController, viewModel)
            }
        }
    }
}

@Composable
fun NavHostContainer(
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: SharedViewModel
) {
    NavHost(navController, startDestination = AppRoute.HOME.route) {
        composable(AppRoute.HOME.route) {
            viewModel.resetTitle()
            HomeComposable(Modifier.padding(paddingValues), navController, viewModel)
        }
        composable(AppRoute.IDENTIFICATION.route) {
            viewModel.changeTitle(stringResource(R.string.identification))
            FragmentTransfer(IdentificationFragment())
        }
        composable(AppRoute.SUPPORT_CHAT.route) {
            viewModel.changeTitle(stringResource(R.string.support_chat))
            FragmentTransfer(SupportChatFragment())
        }
        composable(AppRoute.PUSH_NOTIFICATIONS.route) {
            viewModel.changeTitle(stringResource(R.string.push_notifications))
            FragmentTransfer(PushNotificationsFragment())
        }
        composable(AppRoute.SESSION_ANALYTICS.route) {
            viewModel.changeTitle(stringResource(R.string.session_analytics))
            FragmentTransfer(SessionAnalyticsFragment())
        }
    }
}

@Composable
fun HomeComposable(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SharedViewModel
) {
    val isConfigured by remember { mutableStateOf(DevRev.isConfigured) }
    val isUserIdentified by remember { mutableStateOf(DevRev.isUserIdentified) }
    val isMonitoringEnabled by remember { mutableStateOf(DevRev.isMonitoringEnabled) }
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val buttonItems = listOf(
        ButtonItem(stringResource(R.string.identification)) { navController.navigate(AppRoute.IDENTIFICATION.route) },
        ButtonItem(stringResource(R.string.support_chat)) {
            viewModel.setInScreenTransitioning(true)
            navController.navigate(AppRoute.SUPPORT_CHAT.route)
        },
        ButtonItem(stringResource(R.string.push_notifications)) { navController.navigate(AppRoute.PUSH_NOTIFICATIONS.route) },
        ButtonItem(stringResource(R.string.session_analytics)) { navController.navigate(AppRoute.SESSION_ANALYTICS.route) },
    )

    val debugButtons = listOf(
        ButtonItem(stringResource(R.string.anr)) { viewModel.ANR() },
        ButtonItem(stringResource(R.string.crash)) { viewModel.crash() }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val stateItems = listOf(
            stringResource(R.string.sdk_configured) to isConfigured,
            stringResource(R.string.user_identified) to isUserIdentified,
            stringResource(R.string.session_monitoring_enabled) to isMonitoringEnabled
        )
        textRow(stringResource(R.string.status))
        LazyColumn {
            items(stateItems) { (label, state) ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label)
                    CircularCheckbox(
                        checked = state,
                        onCheckedChange = { }
                    )
                }
            }
            item {
                textRow(stringResource(R.string.feature))
                ButtonsRow(buttonItems)
                textRow(stringResource(R.string.debug))
                ButtonsRow(debugButtons)
                textRow(stringResource(R.string.animation))
                Button(ButtonItem(stringResource(R.string.play_animation)) {coroutineScope.launch {
                    repeat(4) {
                        scale.animateTo(1.2f, animationSpec = spring(dampingRatio = 0.4f))
                        scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.4f))
                    }
                } }, Modifier.scale(scale.value))
            }
        }
    }
}

@Composable
fun FragmentTransfer(fragment: Fragment) {
    val context = LocalContext.current
    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
    LaunchedEffect(Unit) {
        reloadFragment(fragmentManager, fragment, R.id.fragment_container_view)
    }
    AndroidView(
        factory = {
            FragmentContainerView(context).apply {
                id = R.id.fragment_container_view
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun textRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
    )
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .border(2.dp, Color.Gray, CircleShape)
            .background(if (checked) Color.Gray else Color.Transparent, CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun ButtonsRow(
    buttonList: List<ButtonItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        buttonList.forEach { item ->
            Button(item)
        }
    }
}

@Composable
fun Button(item: ButtonItem, additionalModifier: Modifier = Modifier) {
    Button(
        onClick = item.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(additionalModifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = Color.Black
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(item.label)
        }
    }
}

private fun reloadFragment(fragmentManager: FragmentManager, fragment: Fragment, containerId: Int) {
    fragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .commit()
}

data class ButtonItem(val label: String, val onClick: () -> Unit)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SampleApp(viewModel = SharedViewModel())
}
