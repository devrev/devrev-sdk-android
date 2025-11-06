package ai.devrev.sdk.sample.service

import ai.devrev.sdk.DevRev
import ai.devrev.sdk.sample.MainActivity
import ai.devrev.sdk.sample.R
import ai.devrev.sdk.sample.utils.SharedPrefUtil
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class FirebasePushNotificationService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FirebasePushNotificationService"
        private const val CHANNEL_ID = "push_notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        SharedPrefUtil.saveTokenToPreferences(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data["message"]?.let { message ->
            try {
                val messageObject = JSONObject(message)
                val title: String = messageObject.getJSONObject("actor").getString("display_handle")
                sendNotification(title, message)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing message JSON", e)
            }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("notification_pressed", true)
                putExtra("message", messageBody)
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val channelId = CHANNEL_ID
            val notificationManager = getSystemService(NotificationManager::class.java)

            val channelName = getString(R.string.devrev_sdk)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)

            val messageObject = JSONObject(messageBody)

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(messageObject.getString("body"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        } catch (e: Exception) {
            Log.e(TAG, "${getString(R.string.notification_error)}: ${e.message}")
        }
    }

}