package ai.devrev.sdk.sample

import ai.devrev.sdk.sample.viewmodel.PushNotificationsViewModel
import ai.devrev.sdk.sample.utils.DeviceInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class PushNotificationsFragment : Fragment() {

    private val viewModel: PushNotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView = ComposeView(requireContext()).apply {
        setContent {
            PushNotificationsScreen(viewModel)
        }
    }
}

@Composable
fun PushNotificationsScreen(viewModel: PushNotificationsViewModel) {
    val context = LocalContext.current
    val deviceId: String = DeviceInfo.getDeviceId(context)
    val dialogMessage by viewModel.dialogMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeFirebase()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.registerDeviceToken(context, deviceId)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp, vertical = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(stringResource((R.string.register)))
            }
        }

        Button(
            onClick = {
                viewModel.unregisterDevice(context, deviceId)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
            ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(stringResource(R.string.unregister))
            }
        }
    }

    dialogMessage?.let { (isSuccess, message) ->
        AlertDialog(
            onDismissRequest = { viewModel.clearDialogMessage() },
            confirmButton = {
                TextButton(onClick = { viewModel.clearDialogMessage() }) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = { Text(text = if (isSuccess) "Success" else "Error") },
            text = { Text(text = message) }
        )
    }
}