package ai.devrev.sdk.sample.handler

import ai.devrev.sdk.DevRev
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

class NotificationHandler(private val context: Context) {

    fun handleNotificationClick(message: String?) {
        runOnUiThread(context) {
            try {
                if (message != null) {
                    DevRev.processPushNotification(context, message)
                }
            } catch (e: Exception) {
                Log.e("NotificationHandler", "Failed to process notification", e)
            }
        }
    }

    private fun runOnUiThread(context: Context, action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            Handler(Looper.getMainLooper()).post(action)
        }
    }
}