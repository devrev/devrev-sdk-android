package ai.devrev.sdk.sample.viewmodel

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.sample.R
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PushNotificationsViewModel: ViewModel() {
    private val _token = MutableStateFlow<String?>(null)

    private val _dialogMessage = MutableStateFlow<Pair<Boolean, String>?>(null)
    val dialogMessage: StateFlow<Pair<Boolean, String>?> get() = _dialogMessage

    fun initializeFirebase() {
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _token.value = task.result
                } else {
                    Log.e("ERROR", "Fetching FCM token failed", task.exception)
                }
            }
        }
    }

    fun registerDeviceToken(context: Context, deviceId: String) {
        viewModelScope.launch {
            try {
                _token.value?.let { token ->
                    DevRev.registerDeviceToken(context, token, deviceId)
                    _dialogMessage.value = true to context.getString(R.string.register_success)
                }
            } catch (e: Exception) {
                _dialogMessage.value = false to context.getString(R.string.register_error)
            }
        }
    }

    fun unregisterDevice(context: Context, deviceId: String) {
        viewModelScope.launch {
            try {
                DevRev.unregisterDevice(context, deviceId)
                _dialogMessage.value = true to context.getString(R.string.unregister_success)
            } catch (e: Exception) {
                _dialogMessage.value = false to context.getString(R.string.unregister_error)
            }
        }
    }

    fun clearDialogMessage() {
        _dialogMessage.value = null
    }

}